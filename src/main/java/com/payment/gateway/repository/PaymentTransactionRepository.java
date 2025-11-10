package com.payment.gateway.repository;

import com.payment.gateway.model.PaymentTransaction;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentTransactionRepository extends JpaRepository<PaymentTransaction, UUID> {
  Optional<PaymentTransaction> findByIdAndTenantId(UUID id, String tenantId);
}
