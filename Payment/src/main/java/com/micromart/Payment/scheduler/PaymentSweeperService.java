package com.micromart.Payment.scheduler;

import com.micromart.Payment.entity.PaymentRecord;
import com.micromart.Payment.enums.Status;
import com.micromart.Payment.messaging.PaymentEvent;
import com.micromart.Payment.messaging.PaymentStatusPublisher;
import com.micromart.Payment.repository.PaymentRecordRepository;
import com.micromart.Payment.services.PaymentServiceImpl;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PaymentSweeperService {

    private final PaymentRecordRepository paymentRecordRepository;
    private final PaymentStatusPublisher paymentStatusPublisher;
    private static final Logger logger = LoggerFactory.getLogger(PaymentServiceImpl.class);

    public PaymentSweeperService (PaymentRecordRepository paymentRecordRepository,
                                  PaymentStatusPublisher paymentStatusPublisher){
        this.paymentRecordRepository=paymentRecordRepository;
        this.paymentStatusPublisher=paymentStatusPublisher;

    }


    /**
     * This method automatically runs every 30 minutes (1,800,000 milliseconds).
     * It finds any PENDING orders older than 120 minutes and marks them CANCELLED.
     */
    @Scheduled(fixedRate = 1800000)
    @Transactional
    public void sweepAbandonedPayments() {

        logger.info("Sweeper Service waking up to check for abandoned payments...");

        LocalDateTime OneTwentyMinutesAgo = LocalDateTime.now().minusMinutes(120);

        List<PaymentRecord> abandonedRecords = paymentRecordRepository
                .findByStatusAndCreatedAtBefore(Status.PENDING, OneTwentyMinutesAgo);

        if (abandonedRecords.isEmpty()) {
            logger.info(" No abandoned payments found. Going back to sleep.");
            return;
        }

        logger.warn("Found {} abandoned PENDING payments. Cancelling them now...", abandonedRecords.size());

        for (PaymentRecord record : abandonedRecords) {
            record.setStatus(Status.CANCELLED);
            record.setErrorMessage("Payment abandoned by user (Timeout)");

            PaymentEvent event = new PaymentEvent(
                    record.getOrderId(),
                    record.getUserId(),
                    Status.CANCELLED,
                    record.getPaymentMethod()
            );

            paymentStatusPublisher.publishPaymentStatus(event);
            logger.info("Cancelled abandoned payment for Order: {}", record.getOrderId());
        }

        paymentRecordRepository.saveAll(abandonedRecords);

        logger.info("Sweeper Service finished cleaning up.");
    }


}
