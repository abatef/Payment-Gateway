package com.payment.gateway.dto;

import com.payment.gateway.model.enums.PaymentMethod;
import com.payment.gateway.model.enums.PaymentStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentTransactionResponse {

  private UUID id;
  private String tenantId;
  private UUID dealerId;
  private BigDecimal amount;
  private PaymentMethod method;
  private PaymentStatus status;
  private String requestId;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
}
