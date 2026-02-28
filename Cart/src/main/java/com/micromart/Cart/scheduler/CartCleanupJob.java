package com.micromart.Cart.scheduler;

import com.micromart.Cart.repository.CartRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class CartCleanupJob {

    private final CartRepository cartRepository;

    @Scheduled(cron = "0 0 2 * * *")
    @Transactional
    public void cleanupExpiredCarts() {
        LocalDateTime threshold = LocalDateTime.now().minusDays(30);
        log.info("Starting batch cleanup for carts older than {}", threshold);

        int deletedCount;
        int totalDeleted = 0;

        do {
            deletedCount = cartRepository.deleteExpiredBatch(threshold);
            totalDeleted += deletedCount;
            log.debug("Deleted a batch of {} carts...", deletedCount);
        } while (deletedCount > 0);

        log.info("Cleanup finished. Total carts removed: {}", totalDeleted);
    }
}