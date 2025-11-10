package com.payment.gateway.model;

import com.payment.gateway.model.enums.PaymentMethod;
import com.payment.gateway.model.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
    name = "payment_transactions",
    indexes = {
      @Index(name = "idx_tenant_id", columnList = "tenant_id"),
      @Index(name = "idx_dealer_id", columnList = "dealer_id"),
      @Index(name = "idx_request_id", columnList = "request_id"),
      @Index(name = "idx_status", columnList = "status"),
      @Index(name = "idx_tenant_dealer", columnList = "tenant_id, dealer_id")
    })
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentTransaction {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(name = "tenant_id", nullable = false)
  private String tenantId;

  @Column(name = "dealer_id", nullable = false)
  private UUID dealerId;

  @Column(nullable = false, precision = 19, scale = 2)
  private BigDecimal amount;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private PaymentMethod method;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private PaymentStatus status;

  @Column(name = "request_id", nullable = false, unique = true)
  private String requestId;

  @CreationTimestamp
  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at", nullable = false)
  private LocalDateTime updatedAt;
}
