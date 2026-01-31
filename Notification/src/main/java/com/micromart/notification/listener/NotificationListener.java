package com.micromart.notification.listener;

import com.micromart.notification.channels.EmailNotificationChannel;
import com.micromart.notification.channels.NotificationChannel;
import com.micromart.notification.configuration.RabbitMQConfig;
import com.micromart.notification.factory.NotificationFactory;
import com.micromart.notification.model.PasswordResetEventDto;
import com.micromart.notification.model.ReactivationEvent;
import com.micromart.notification.model.UserCreatedEventDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;

@Component
public class NotificationListener {

    private static final Logger logger = LoggerFactory.getLogger(NotificationListener.class);
    private final NotificationFactory notificationFactory;
    private final Environment environment;

    @Value("${app.frontend.url}")
    private String frontendBaseUrl;

   public NotificationListener(NotificationFactory notificationFactory,Environment environment){
       this.notificationFactory = notificationFactory;
       this.environment = environment;
   }

    @RabbitListener(queues = RabbitMQConfig.USER_CREATED_QUEUE)
    public void handleUserCreatedEvent(UserCreatedEventDto event) {
        logger.info("📨 Received event for: {}", event.getEmail());

        // --- EMAIL SECTION ---
        try {
            NotificationChannel channel = notificationFactory.getChannel("EMAIL");
            if (channel instanceof EmailNotificationChannel emailChannel) {

                emailChannel.sendHtmlVerificationEmail(
                        event.getEmail(),
                        event.getFirstName(),
                        event.getVerificationToken()
                );

            } else {
                logger.warn("⚠️ Channel is not EmailNotificationChannel, sending plain text.");
                channel.sendNotification(event.getEmail(), "Welcome", "Please verify your account.");
            }

        } catch (Exception e) {
            logger.error("❌ Failed to send Welcome Email", e);
        }

        // --- SMS SECTION  ---
        if (event.getMobileNumber() != null && !event.getMobileNumber().isEmpty()) {
            try {
                String smsBody = "Welcome to Micromart, " + event.getFirstName() + "! " +
                        "Your verification code is: " + event.getVerificationToken();

                NotificationChannel smsChannel = notificationFactory.getChannel("SMS");
                smsChannel.sendNotification(event.getMobileNumber(), "", smsBody);
                logger.info("SMS sent to {}", event.getMobileNumber());
            } catch (Exception e) {
                logger.error("Failed to send Welcome SMS", e);
            }
        } else {
            logger.warn("No phone number found for user {}. Skipping SMS.", event.getEmail());
        }
    }
    @RabbitListener(queues = RabbitMQConfig.PASSWORD_RESET_QUEUE)
    public void handlePasswordResetEvent(PasswordResetEventDto event) {
        logger.info("🔑 Received Password Reset request for: {}", event.getEmail());

        try {
            NotificationChannel channel = notificationFactory.getChannel("EMAIL");
            if (channel instanceof EmailNotificationChannel emailChannel) {
                emailChannel.sendHtmlPasswordResetEmail(
                        event.getEmail(),
                        event.getFirstName(),
                        event.getPasswordResetToken()
                );

            } else {
                channel.sendNotification(event.getEmail(), "Reset Password", "Your token is: " + event.getPasswordResetToken());
            }

        } catch (Exception e) {
            logger.error("❌ Failed to send Password Reset Email", e);
        }
    }

    @RabbitListener(queues = RabbitMQConfig.USER_REACTIVATION_QUEUE)
    public void handleReactivationEvent(ReactivationEvent event) {
        logger.info("💌 Received reactivation request for: {}", event.getEmail());

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
                logger.warn("⚠️ Channel is not EmailNotificationChannel");
            }
        } catch (Exception e) {
            logger.error("❌ Failed to send Reactivation Email", e);
        }
    }

}

