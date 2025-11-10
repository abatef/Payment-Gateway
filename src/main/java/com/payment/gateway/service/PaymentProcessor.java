package com.payment.gateway.service;

import com.payment.gateway.model.PaymentTransaction;
import com.payment.gateway.model.enums.PaymentStatus;
import com.payment.gateway.repository.PaymentTransactionRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentProcessor {

  private final PaymentTransactionRepository paymentRepository;

  @Value("${payment.processing.delay-seconds:5}")
  private int delaySeconds;

  @Scheduled(fixedRateString = "${payment.processing.scheduler-fixed-rate:10000}")
  @Transactional
  public void processPendingPayments() {
    LocalDateTime thresholdTime = LocalDateTime.now().minusSeconds(delaySeconds);

    List<PaymentTransaction> pendingPayments =
        paymentRepository.findPendingPaymentsOlderThan(PaymentStatus.PENDING, thresholdTime);

    if (!pendingPayments.isEmpty()) {
      log.info("Processing {} pending payments", pendingPayments.size());

      for (PaymentTransaction payment : pendingPayments) {
        try {
          payment.setStatus(PaymentStatus.SUCCESS);
          paymentRepository.save(payment);

          log.info(
              "Updated payment {} to SUCCESS for tenant: {} (created at: {})",
              payment.getId(),
              payment.getTenantId(),
              payment.getCreatedAt());
        } catch (Exception e) {
          log.error("Failed to process payment {}: {}", payment.getId(), e.getMessage(), e);
        }
      }
    }
  }
}
