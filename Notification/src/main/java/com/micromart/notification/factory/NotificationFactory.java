package com.micromart.notification.factory;

import com.micromart.notification.channels.NotificationChannel;
import com.micromart.notification.channels.SmsNotificationChannel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import com.micromart.notification.channels.EmailNotificationChannel;

@Component
//@RequiredArgsConstructor
public class NotificationFactory {

    private final EmailNotificationChannel emailChannel;
    private final SmsNotificationChannel smsChannel;

    public NotificationFactory(EmailNotificationChannel emailChannel, SmsNotificationChannel smsChannel) {
        this.emailChannel = emailChannel;
        this.smsChannel = smsChannel;
    }

    public NotificationChannel getChannel(String type) {
        if ("USER_CREATED".equalsIgnoreCase(type) || "EMAIL".equalsIgnoreCase(type)) {
            return emailChannel;
        }
         else if ("SMS".equals(type)) return smsChannel;

        throw new IllegalArgumentException("Unknown notification type: " + type);
    }
}