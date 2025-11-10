package com.payment.gateway.repository;

import com.payment.gateway.model.PaymentTransaction;
import com.payment.gateway.model.enums.PaymentStatus;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentTransactionRepository extends JpaRepository<PaymentTransaction, UUID> {
  Optional<PaymentTransaction> findByIdAndTenantId(UUID id, String tenantId);

  @Query(
      "SELECT p FROM PaymentTransaction p WHERE p.status = :status "
          + "AND p.createdAt <= :thresholdTime")
  List<PaymentTransaction> findPendingPaymentsOlderThan(
      @Param("status") PaymentStatus status, @Param("thresholdTime") LocalDateTime thresholdTime);
}
