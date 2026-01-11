package com.micromart.notification.channels;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EmailNotificationChannel implements NotificationChannel {
    private static final Logger logger = LoggerFactory.getLogger(EmailNotificationChannel.class);
    private final JavaMailSender javaMailSender;

    private final Environment environment;

    @Override
    public void sendNotification(String to, String subject, String content) {
        logger.info("📧 Preparing to send email to: {}", to);

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(environment.getProperty("spring.mail.from"));
            message.setTo(to);
            message.setSubject(subject);
            message.setText(content);

            javaMailSender.send(message);
            logger.info("✅ Email sent successfully to {}", to);

        } catch (Exception e) {
            logger.error("❌ Failed to send email to {}: {}", to, e.getMessage());
        }
    }
}