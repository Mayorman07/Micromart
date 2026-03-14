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
 * Service component responsible for consuming asynchronous messages from RabbitMQ.
 * This listener acts as the central hub for dispatching system notifications (Email, SMS).
 * * NOTE: Internal try-catch blocks are omitted to allow exceptions to bubble up to Spring AMQP.
 * This triggers the configured retry logic and eventually moves failed messages to the
 * Dead Letter Queue (DLQ) for manual intervention.
 */
@Component
public class NotificationListener {

    private final NotificationFactory notificationFactory;
    private final Environment environment;
    private static final Logger log = LoggerFactory.getLogger(NotificationListener.class);

    private NotificationListener(NotificationFactory notificationFactory, Environment environment){
        this.notificationFactory=notificationFactory;
        this.environment=environment;
    }

    /**
     * Handles new user registration events.
     * Dispatches a verification email and an optional welcome SMS.
     *
     * @param event DTO containing user registration details and verification token.
     */
    @RabbitListener(queues = RabbitMQConfig.USER_CREATED_QUEUE)
    public void handleUserCreatedEvent(UserCreatedEventDto event) {
        log.info(" Processing user creation notification for: {}", event.getEmail());

        // Send Verification Email
        NotificationChannel emailChannel = notificationFactory.getChannel("EMAIL");
        if (emailChannel instanceof EmailNotificationChannel channel) {
            channel.sendHtmlVerificationEmail(
                    event.getEmail(),
                    event.getFirstName(),
                    event.getVerificationToken()
            );
        }

        // Send Welcome SMS if a mobile number is provided
        if (event.getMobileNumber() != null && !event.getMobileNumber().isBlank()) {
            String smsBody = String.format("Welcome to Micromart, %s! Your verification code is: %s",
                    event.getFirstName(), event.getVerificationToken());

            NotificationChannel smsChannel = notificationFactory.getChannel("SMS");
            smsChannel.sendNotification(event.getMobileNumber(), "", smsBody);
            log.info("📱 Welcome SMS dispatched to {}", event.getMobileNumber());
        }
    }

    /**
     * Handles password reset requests.
     * Dispatches an email containing the secure reset link.
     *
     * @param event DTO containing user email and the generated reset token.
     */
    @RabbitListener(queues = RabbitMQConfig.PASSWORD_RESET_QUEUE)
    public void handlePasswordResetEvent(PasswordResetEventDto event) {
        log.info(" Processing password reset notification for: {}", event.getEmail());

        NotificationChannel channel = notificationFactory.getChannel("EMAIL");
        if (channel instanceof EmailNotificationChannel emailChannel) {
            emailChannel.sendHtmlPasswordResetEmail(
                    event.getEmail(),
                    event.getFirstName(),
                    event.getPasswordResetToken()
            );
        }
    }

    /**
     * Handles user reactivation (re-engagement) events.
     * Processes a Thymeleaf template to generate a "We Miss You" email.
     *
     * @param event DTO containing inactive user details.
     */
    @RabbitListener(queues = RabbitMQConfig.USER_REACTIVATION_QUEUE)
    public void handleReactivationEvent(ReactivationEvent event) {
        log.info("Processing user reactivation notification for: {}", event.getEmail());

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
        }
    }

    /**
     * Handles payment status updates from the Payment Service.
     * Triggers EMAIL #1: "Payment Received / Receipt"
     */
    @RabbitListener(queues = RabbitMQConfig.PAYMENT_NOTIFICATION_QUEUE)
    public void handlePaymentEvent(PaymentEvent event) {
        log.info("Processing payment notification | Order: {} | Status: {}",
                event.getOrderId(), event.getStatus());

        NotificationChannel channel = notificationFactory.getChannel("EMAIL");
        if (channel instanceof EmailNotificationChannel emailChannel) {

            // Dispatch appropriate template based on the event status
            switch (event.getStatus()) {
                case PAID -> emailChannel.sendPaymentSuccessEmail(event.getUserId(), event.getOrderId());
                case CANCELLED -> emailChannel.sendPaymentCancelledEmail(event.getUserId(), event.getOrderId());
                default -> log.warn(" Received unhandled payment status: {} for order: {}",
                        event.getStatus(), event.getOrderId());
            }
        }
    }

    /**
     * Handles detailed order receipt events published by the Order Service.
     * Triggers EMAIL #2: "Your order is on its way!"
     */
    @RabbitListener(bindings = @org.springframework.amqp.rabbit.annotation.QueueBinding(
            value = @org.springframework.amqp.rabbit.annotation.Queue(value = "notification.receipt.queue", durable = "true"),
            exchange = @org.springframework.amqp.rabbit.annotation.Exchange(value = "micromart.exchange", type = "topic"),
            key = "notification.receipt"
    ))
    public void handleOrderReceiptEvent(com.micromart.notification.model.OrderReceiptEvent event) {
        log.info("Processing order fulfillment notification for Order: {}", event.getOrderNumber());

        NotificationChannel channel = notificationFactory.getChannel("EMAIL");
        if (channel instanceof EmailNotificationChannel emailChannel) {
            emailChannel.sendOrderReceiptEmail(event);
        }
    }
}