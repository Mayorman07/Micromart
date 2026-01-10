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

        // 1. Construct the Email Content
        String subject = "Welcome to Micromart! Verify your Email";
        String verificationLink = environment.getProperty("app.gateway.url") + "/users/verify?token="
                + event.getVerificationToken();

        String body = "Hello " + event.getFirstName() + ",\n\n" +
                "Welcome to Micromart! Please click the link below to activate your account:\n" +
                verificationLink + "\n\n" +
                "See you soon!";

        // 2. Use the Factory to get the right sender
        NotificationChannel channel = notificationFactory.getChannel("EMAIL");

        // 3. Send it!
        channel.sendNotification(event.getEmail(), subject, body);
    }
}
