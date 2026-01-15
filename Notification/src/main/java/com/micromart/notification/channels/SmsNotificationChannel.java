package com.micromart.notification.channels;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SmsNotificationChannel implements NotificationChannel {

    private static final Logger logger = LoggerFactory.getLogger(SmsNotificationChannel.class);
    @Value("${twilio.account.sid}")
    private String accountSid;

    @Value("${twilio.auth.token}")
    private String authToken;
    @Value("${twilio.phone.number}")
    private String fromPhoneNumber;

    @PostConstruct
    public void initTwilio() {
        try {
            Twilio.init(accountSid, authToken);
            logger.info("Twilio Initialized");
        } catch (Exception e) {
            logger.error("Twilio Init Failed: " + e.getMessage());
        }
    }

    @Override
    public void sendNotification(String to, String subject, String content) {
        try {

            Message message = Message.creator(
                    new PhoneNumber(to),
                    new PhoneNumber(fromPhoneNumber),
                    content
            ).create();

            logger.info("SMS Sent! SID: " + message.getSid());

        } catch (Exception e) {
            logger.error("Failed to send SMS: " + e.getMessage());
        }
    }
}