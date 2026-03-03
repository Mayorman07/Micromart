package com.micromart.messaging;

import com.micromart.configuration.RabbitMQConfig;
import com.micromart.models.data.PasswordResetEventDto;
import com.micromart.models.data.UserCreatedEventDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

/**
 * Service responsible for publishing domain events to RabbitMQ.
 * Acts as the primary message producer for asynchronous inter-service communication.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MessagePublisher {

    private final RabbitTemplate rabbitTemplate;
    private static final Logger logger = LoggerFactory.getLogger(MessagePublisher.class);


    /**
     * Publishes an event when a new user account is successfully created.
     * Triggers downstream services (e.g., Notification Service) to send welcome and verification emails.
     *
     * @param eventDto The data transfer object containing new user details.
     */
    public void sendUserCreatedEvent(UserCreatedEventDto eventDto) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE_NAME,
                RabbitMQConfig.ROUTING_KEY,
                eventDto
        );
        logger.info("User created event published to RabbitMQ for: {}", eventDto.getEmail());
    }

    /**
     * Publishes an event to trigger the generation and dispatch of a password reset link.
     *
     * @param eventDto The data transfer object containing the password reset details.
     */
    public void sendPasswordResetEvent(PasswordResetEventDto eventDto) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE_NAME,
                RabbitMQConfig.PASSWORD_RESET_ROUTING_KEY,
                eventDto
        );
        logger.info("Password reset event published to RabbitMQ for: {}", eventDto.getEmail());
    }

    /**
     * Publishes an event recording a password reset attempt.
     * Often consumed for security auditing and rate-limiting monitoring.
     *
     * @param event The event detailing the reset attempt.
     */
    public void sendPasswordResetAttempt(PasswordResetRequestEvent event) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE_NAME,
                RabbitMQConfig.PASSWORD_RESET_ATTEMPT_ROUTING_KEY,
                event
        );
        logger.info("Password reset attempt event published for: {}", event.getEmail());
    }

    /**
     * Publishes an event to trigger a reactivation campaign for a dormant user.
     *
     * @param reactivationEvent The event detailing the user targeted for reactivation.
     */
    public void sendReactivationEvent(ReactivationEvent reactivationEvent) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE_NAME,
                RabbitMQConfig.SEND_REACTIVATION_EMAILS_ROUTING_KEY,
                reactivationEvent
        );
        logger.info("Reactivation event published for: {}", reactivationEvent.getEmail());
    }
}