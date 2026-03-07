package com.micromart.notification.listener;

import com.micromart.notification.channels.EmailNotificationChannel;
import com.micromart.notification.channels.NotificationChannel;
import com.micromart.notification.configuration.RabbitMQConfig;
import com.micromart.notification.factory.NotificationFactory;
import com.micromart.notification.model.PasswordResetEventDto;
import com.micromart.notification.model.PaymentEvent;
import com.micromart.notification.model.ReactivationEvent;
import com.micromart.notification.model.UserCreatedEventDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;

/**
 * Consumes asynchronous messages from RabbitMQ queues and dispatches notifications.
 * Acts as the primary entry point for event-driven communications within the Notification Service.
 */
@Component
public class NotificationListener {

    private static final Logger logger = LoggerFactory.getLogger(NotificationListener.class);

    private final NotificationFactory notificationFactory;
    private final Environment environment;

    /**
     * Constructs the NotificationListener with necessary dependencies.
     *
     * @param notificationFactory Factory for instantiating the appropriate notification channels.
     * @param environment         Application environment for accessing configuration properties.
     */
    public NotificationListener(NotificationFactory notificationFactory, Environment environment) {
        this.notificationFactory = notificationFactory;
        this.environment = environment;
    }

    /**
     * Consumes messages from the user-created queue to dispatch welcome and verification communications.
     * Attempts to send both an HTML verification email and an SMS text message.
     *
     * @param event The data transfer object containing the new user's details and verification token.
     */
    @RabbitListener(queues = RabbitMQConfig.USER_CREATED_QUEUE)
    public void handleUserCreatedEvent(UserCreatedEventDto event) {
        logger.info("Received user creation event for: {}", event.getEmail());

        // Process Email Notification
        try {
            NotificationChannel channel = notificationFactory.getChannel("EMAIL");
            if (channel instanceof EmailNotificationChannel emailChannel) {
                emailChannel.sendHtmlVerificationEmail(
                        event.getEmail(),
                        event.getFirstName(),
                        event.getVerificationToken()
                );
            } else {
                logger.warn("Resolved channel is not an EmailNotificationChannel. Falling back to plain text.");
                channel.sendNotification(event.getEmail(), "Welcome", "Please verify your account.");
            }
        } catch (Exception e) {
            logger.error("Failed to send welcome email to {}: {}", event.getEmail(), e.getMessage(), e);
        }

        // Process SMS Notification
        if (event.getMobileNumber() != null && !event.getMobileNumber().isEmpty()) {
            try {
                String smsBody = String.format("Welcome to Micromart, %s! Your verification code is: %s",
                        event.getFirstName(), event.getVerificationToken());

                NotificationChannel smsChannel = notificationFactory.getChannel("SMS");
                smsChannel.sendNotification(event.getMobileNumber(), "", smsBody);

                logger.info("Welcome SMS successfully dispatched to {}", event.getMobileNumber());
            } catch (Exception e) {
                logger.error("Failed to send welcome SMS to {}: {}", event.getMobileNumber(), e.getMessage(), e);
            }
        } else {
            logger.warn("No valid mobile number found for user {}. Skipping SMS dispatch.", event.getEmail());
        }
    }

    /**
     * Consumes messages from the password-reset queue to dispatch secure reset links.
     *
     * @param event The data transfer object containing the user's email and reset token.
     */
    @RabbitListener(queues = RabbitMQConfig.PASSWORD_RESET_QUEUE)
    public void handlePasswordResetEvent(PasswordResetEventDto event) {
        logger.info("Received password reset request for: {}", event.getEmail());

        try {
            NotificationChannel channel = notificationFactory.getChannel("EMAIL");
            if (channel instanceof EmailNotificationChannel emailChannel) {
                emailChannel.sendHtmlPasswordResetEmail(
                        event.getEmail(),
                        event.getFirstName(),
                        event.getPasswordResetToken()
                );
            } else {
                logger.warn("Resolved channel is not an EmailNotificationChannel. Falling back to plain text.");
                channel.sendNotification(event.getEmail(), "Reset Password", "Your token is: " + event.getPasswordResetToken());
            }
        } catch (Exception e) {
            logger.error("Failed to send password reset email to {}: {}", event.getEmail(), e.getMessage(), e);
        }
    }

    /**
     * Consumes messages from the user-reactivation queue to dispatch engagement emails.
     *
     * @param event The data transfer object containing the inactive user's details.
     */
    @RabbitListener(queues = RabbitMQConfig.USER_REACTIVATION_QUEUE)
    public void handleReactivationEvent(ReactivationEvent event) {
        logger.info("Received reactivation request for: {}", event.getEmail());

        try {
            NotificationChannel channel = notificationFactory.getChannel("EMAIL");
            if (channel instanceof EmailNotificationChannel emailChannel) {
                Context context = new Context();
                context.setVariable("firstName", event.getFirstName());
                context.setVariable("loginUrl", environment.getProperty("app.frontend.url") + "/login");

                emailChannel.sendHtmlEmail(
                        event.getEmail(),
                        "We Miss You at MicroMart!",
                        "reactivation-email",
                        context
                );
            } else {
                logger.warn("Resolved channel is not an EmailNotificationChannel. Skipping reactivation email.");
            }
        } catch (Exception e) {
            logger.error("Failed to send reactivation email to {}: {}", event.getEmail(), e.getMessage(), e);
        }
    }

    @RabbitListener(queues = RabbitMQConfig.PAYMENT_NOTIFICATION_QUEUE)
    public void handlePaymentEvent(PaymentEvent event) {
        logger.info("Received payment event for Order: {} - Status: {}", event.getOrderId(), event.getStatus());

        try {
            NotificationChannel channel = notificationFactory.getChannel("EMAIL");
            if (channel instanceof EmailNotificationChannel emailChannel) {

                if ("PAID".equals(event.getStatus().name())) {
                    emailChannel.sendPaymentSuccessEmail(event.getUserId(), event.getOrderId());
                } else if ("CANCELLED".equals(event.getStatus().name())) {
                    emailChannel.sendPaymentCancelledEmail(event.getUserId(), event.getOrderId());
                }

            }
        } catch (Exception e) {
            logger.error("Failed to process payment notification for order {}: {}", event.getOrderId(), e.getMessage());
        }
    }
}