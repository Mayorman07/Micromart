package com.micromart.Payment.repository;

import com.micromart.Payment.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.micromart.Payment.entity.PaymentRecord;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRecordRepository extends JpaRepository<PaymentRecord, Long> {

    // Find by  internal Order ID (for webhook correlation)
    Optional<PaymentRecord> findByOrderId(String orderId);

    //  Find by Stripe Session ID (for idempotency + webhook safety)
    Optional<PaymentRecord> findByExternalReference(String externalReference);

    List<PaymentRecord> findByStatusIn(List<Status> statuses);

    // Check if a payment already exists for this order + status
    boolean existsByOrderIdAndStatus(String orderId, Status status);

    List<PaymentRecord> findByStatusAndCreatedAtBefore(Status status, LocalDateTime cutoffTime);
}
