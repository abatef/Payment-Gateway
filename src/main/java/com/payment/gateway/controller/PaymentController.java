package com.payment.gateway.controller;

import com.payment.gateway.dto.PaymentInitiateRequest;
import com.payment.gateway.dto.PaymentTransactionResponse;
import com.payment.gateway.exception.BadRequestException;
import com.payment.gateway.service.PaymentService;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {

  private final PaymentService paymentService;

  @PostMapping("/initiate")
  public ResponseEntity<PaymentTransactionResponse> initiatePayment(
      @Valid @RequestBody PaymentInitiateRequest request,
      @RequestHeader(value = "Idempotency-Key") String idempotencyKey) {

    if (idempotencyKey == null || idempotencyKey.trim().isEmpty()) {
      throw new BadRequestException("Idempotency-Key header is required");
    }

    log.info(
        "Initiating payment for dealer: {} with idempotency key: {}",
        request.getDealerId(),
        idempotencyKey);

    PaymentTransactionResponse response = paymentService.initiatePayment(request, idempotencyKey);

    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @GetMapping("/{id}")
  public ResponseEntity<PaymentTransactionResponse> getPaymentStatus(@PathVariable UUID id) {
    log.info("Fetching payment status for transaction: {}", id);

    PaymentTransactionResponse response = paymentService.getPaymentStatus(id);

    return ResponseEntity.ok(response);
  }
}
