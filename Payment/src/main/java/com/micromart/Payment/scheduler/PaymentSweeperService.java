package com.micromart.Payment.scheduler;

import com.micromart.Payment.entity.PaymentRecord;
import com.micromart.Payment.enums.Status;
import com.micromart.Payment.repository.PaymentRecordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentSweeperService {

    private final PaymentRecordRepository paymentRecordRepository;

    /**
     * This method automatically runs every 30 minutes (1,800,000 milliseconds).
     * It finds any PENDING orders older than 120 minutes and marks them CANCELLED.
     */
    @Scheduled(fixedRate = 1800000)
    @Transactional
    public void sweepAbandonedPayments() {
        log.info("Sweeper Service waking up to check for abandoned payments...");

        LocalDateTime OneTwentyMinutesAgo = LocalDateTime.now().minusMinutes(120);

        List<PaymentRecord> abandonedRecords = paymentRecordRepository
                .findByStatusAndCreatedAtBefore("PENDING", OneTwentyMinutesAgo);

        if (abandonedRecords.isEmpty()) {
            log.info(" No abandoned payments found. Going back to sleep.");
            return;
        }

        log.warn("Found {} abandoned PENDING payments. Cancelling them now...", abandonedRecords.size());

        for (PaymentRecord record : abandonedRecords) {
            record.setStatus(Status.CANCELLED);
            record.setErrorMessage("Payment abandoned by user (Timeout)");

            // rabbitMQPublisher.sendPaymentCancelledEvent(record.getOrderId());

            log.info("Cancelled abandoned payment for Order: {}", record.getOrderId());
        }

        paymentRecordRepository.saveAll(abandonedRecords);

        log.info("Sweeper Service finished cleaning up.");
    }
}
