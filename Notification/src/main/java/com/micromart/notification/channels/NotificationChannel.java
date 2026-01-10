package com.micromart.notification.channels;

public interface NotificationChannel {
    void sendNotification(String to, String subject, String content);
}
