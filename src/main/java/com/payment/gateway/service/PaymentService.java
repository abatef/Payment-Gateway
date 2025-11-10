package com.payment.gateway.service;

import com.payment.gateway.dto.PaymentInitiateRequest;
import com.payment.gateway.dto.PaymentTransactionResponse;
import com.payment.gateway.exception.CrossTenantAccessException;
import com.payment.gateway.exception.ForbiddenException;
import com.payment.gateway.exception.ResourceNotFoundException;
import com.payment.gateway.model.PaymentTransaction;
import com.payment.gateway.model.enums.PaymentStatus;
import com.payment.gateway.repository.PaymentTransactionRepository;
import com.payment.gateway.security.TenantContext;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

  private final PaymentTransactionRepository paymentRepository;
  private final IdempotencyService idempotencyService;

  @Transactional
  public PaymentTransactionResponse initiatePayment(
      PaymentInitiateRequest request, String idempotencyKey) {
    String tenantId = TenantContext.getTenantId();

    UUID existingTransactionId =
        idempotencyService.getTransactionIdByIdempotencyKey(idempotencyKey);
    if (existingTransactionId != null) {
      log.info("Returning existing transaction for idempotency key: {}", idempotencyKey);
      PaymentTransaction existingTransaction =
          paymentRepository
              .findById(existingTransactionId)
              .orElseThrow(() -> new ResourceNotFoundException("Transaction not found"));

      if (!existingTransaction.getTenantId().equals(tenantId)) {
        throw new CrossTenantAccessException("Access denied to this transaction");
      }

      return mapToResponse(existingTransaction);
    }

    PaymentTransaction transaction =
        PaymentTransaction.builder()
            .tenantId(tenantId)
            .dealerId(request.getDealerId())
            .amount(request.getAmount())
            .method(request.getMethod())
            .status(PaymentStatus.PENDING)
            .requestId(idempotencyKey)
            .build();

    transaction = paymentRepository.save(transaction);
    log.info("Created new payment transaction: {} for tenant: {}", transaction.getId(), tenantId);

    idempotencyService.saveIdempotentTransaction(idempotencyKey, transaction);

    return mapToResponse(transaction);
  }

  @Transactional(readOnly = true)
  public PaymentTransactionResponse getPaymentStatus(UUID paymentId) {
    String tenantId = TenantContext.getTenantId();

    PaymentTransaction transaction =
        paymentRepository
            .findByIdAndTenantId(paymentId, tenantId)
            .orElseThrow(
                () -> {
                  if (paymentRepository.existsById(paymentId)) {
                    log.warn(
                        "Cross-tenant access attempt: tenant {} tried to access payment {}",
                        tenantId,
                        paymentId);
                    return new CrossTenantAccessException("Access denied to this transaction");
                  }
                  return new ResourceNotFoundException("Payment transaction not found");
                });

    return mapToResponse(transaction);
  }

  private PaymentTransactionResponse mapToResponse(PaymentTransaction transaction) {
    return PaymentTransactionResponse.builder()
        .id(transaction.getId())
        .tenantId(transaction.getTenantId())
        .dealerId(transaction.getDealerId())
        .amount(transaction.getAmount())
        .method(transaction.getMethod())
        .status(transaction.getStatus())
        .requestId(transaction.getRequestId())
        .createdAt(transaction.getCreatedAt())
        .updatedAt(transaction.getUpdatedAt())
        .build();
  }
}
