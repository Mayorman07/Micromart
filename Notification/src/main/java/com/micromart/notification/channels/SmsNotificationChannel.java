package com.micromart.notification.channels;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SmsNotificationChannel implements NotificationChannel{
    private static final Logger logger = LoggerFactory.getLogger(EmailNotificationChannel.class);
    @Override
    public void sendNotification(String to, String subject, String content) {

        logger.info("📧 Preparing to send sms to: {}", to);

    }
}
