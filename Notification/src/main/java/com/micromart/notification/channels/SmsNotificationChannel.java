package com.micromart.notification.channels;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SmsNotificationChannel implements NotificationChannel {

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
            System.out.println("Twilio Initialized");
        } catch (Exception e) {
            System.err.println("Twilio Init Failed: " + e.getMessage());
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

            System.out.println("SMS Sent! SID: " + message.getSid());

        } catch (Exception e) {
            System.err.println("Failed to send SMS: " + e.getMessage());
        }
    }
}