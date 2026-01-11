package com.micromart.notification.listener;

import com.micromart.notification.channels.EmailNotificationChannel;
import com.micromart.notification.channels.NotificationChannel;
import com.micromart.notification.factory.NotificationFactory;
import com.micromart.notification.model.UserCreatedEventDto;
import configuration.RabbitMQConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationListener {

    private static final Logger logger = LoggerFactory.getLogger(NotificationListener.class);

    private final NotificationFactory notificationFactory;
    private final Environment environment;

    @RabbitListener(queues = RabbitMQConfig.USER_CREATED_QUEUE)
    public void handleUserCreatedEvent(UserCreatedEventDto event) {
        logger.info("📨 Received event for: {}", event.getEmail());
        String verificationLink = environment.getProperty("app.gateway.url") + "/users/verify?token="
                + event.getVerificationToken();

       try{
           String emailSubject = "Welcome to Micromart! Verify your Email";
        String emailBody = "Howdy " + event.getFirstName() + ",\n\n" +
                "Welcome to Micromart! Please click the link below to activate your account:\n" +
                verificationLink + "\n\n" +
                "See you soon!";

        NotificationChannel emailChannel = notificationFactory.getChannel("EMAIL");

        emailChannel.sendNotification(event.getEmail(), emailSubject, emailBody);
           logger.info("Email sent to {}", event.getEmail());
    } catch(Exception e) {
           logger.error("Failed to send Welcome Email", e);
     }

        if (event.getMobileNumber() != null && !event.getMobileNumber().isEmpty()) {
            try {
                String smsBody = "Welcome to Micromart, " + event.getFirstName() + "! " +
                        "Your verification code is : " + event.getVerificationToken();

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

