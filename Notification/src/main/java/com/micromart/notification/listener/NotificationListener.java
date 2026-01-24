package com.micromart.notification.listener;

import com.micromart.notification.channels.EmailNotificationChannel;
import com.micromart.notification.channels.NotificationChannel;
import com.micromart.notification.configuration.RabbitMQConfig;
import com.micromart.notification.factory.NotificationFactory;
import com.micromart.notification.model.UserCreatedEventDto;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class NotificationListener {

    private static final Logger logger = LoggerFactory.getLogger(NotificationListener.class);
    private final NotificationFactory notificationFactory;
    private final Environment environment;

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
            if (channel instanceof EmailNotificationChannel) {

                EmailNotificationChannel emailChannel = (EmailNotificationChannel) channel;
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
}

