package com.micromart.notification.listener;

import com.micromart.notification.channels.NotificationChannel;
import com.micromart.notification.factory.NotificationFactory;
import com.micromart.notification.model.UserCreatedEventDto;
import configuration.RabbitMQConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationListener {

    private final NotificationFactory notificationFactory;
    private final Environment environment;

    @RabbitListener(queues = RabbitMQConfig.USER_CREATED_QUEUE)
    public void handleUserCreatedEvent(UserCreatedEventDto event) {
        log.info("📨 Received event for: {}", event.getEmail());
        String verificationLink = environment.getProperty("app.gateway.url") + "/users/verify?token="
                + event.getVerificationToken();

       try{ String emailSubject = "Welcome to Micromart! Verify your Email";
        String emailBody = "Hello " + event.getFirstName() + ",\n\n" +
                "Welcome to Micromart! Please click the link below to activate your account:\n" +
                verificationLink + "\n\n" +
                "See you soon!";

        NotificationChannel emailChannel = notificationFactory.getChannel("EMAIL");

        emailChannel.sendNotification(event.getEmail(), emailSubject, emailBody);
        log.info("Email sent to {}", event.getEmail());
    } catch(Exception e) {
        log.error("Failed to send Welcome Email", e);
     }

        if (event.getMobileNumber() != null && !event.getMobileNumber().isEmpty()) {
            try {
                String smsBody = "Welcome to Micromart, " + event.getFirstName() + "! " +
                        "Verify here: " + event.getVerificationToken();

                NotificationChannel smsChannel = notificationFactory.getChannel("SMS");
                smsChannel.sendNotification(event.getMobileNumber(), "", smsBody);
                log.info("SMS sent to {}", event.getMobileNumber());
            } catch (Exception e) {
                log.error("Failed to send Welcome SMS", e);
            }
        } else {
            log.warn("No phone number found for user {}. Skipping SMS.", event.getEmail());
        }
    }
}

