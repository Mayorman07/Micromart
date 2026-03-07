package com.micromart.notification.listener;

import com.micromart.notification.configuration.RabbitMQConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * Monitor for the Notification Dead Letter Queue (DLQ).
 * This component handles messages that have failed all retry attempts.
 */
@Component
public class DLQListener {

    private static final Logger log = LoggerFactory.getLogger(DLQListener.class);

    /**
     * Listens to the DLQ to log failures and provide visibility into undelivered notifications.
     * * @param failedMessage The original message with failure metadata in headers.
     */
    @RabbitListener(queues = RabbitMQConfig.NOTIFICATION_DLQ)
    public void handleDeadLetter(Message failedMessage) {
        String originalQueue = failedMessage.getMessageProperties().getConsumerQueue();

        // Extract the reason for the failure from RabbitMQ headers
        Object deathHeader = failedMessage.getMessageProperties().getHeader("x-first-death-reason");
        String reason = (deathHeader != null) ? deathHeader.toString() : "Unknown failure";

        log.error("DEAD LETTER ALERT: Notification Permanent Failure");
        log.error("Source Queue: [{}]", originalQueue);
        log.error("Reason: {}", reason);
        log.error("Payload: {}", new String(failedMessage.getBody()));
    }
}
