package com.micromart.notification.channels;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
//@RequiredArgsConstructor
public class EmailNotificationChannel implements NotificationChannel {

    private static final Logger logger = LoggerFactory.getLogger(EmailNotificationChannel.class);
    private final JavaMailSender javaMailSender;
    private final Environment environment;

    public EmailNotificationChannel(JavaMailSender javaMailSender, Environment environment) {
        this.javaMailSender = javaMailSender;
        this.environment = environment;
    }

    @Override
    public void sendNotification(String to, String subject, String content) {
        logger.info("📧 Preparing to send email to: {}", to);

        try {
            // 1. Create a MimeMessage
            MimeMessage message = javaMailSender.createMimeMessage();

            // 2. Use the Helper (true = multipart, StandardCharsets.UTF_8 = encoding)
            MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, StandardCharsets.UTF_8.name());

            // 3. Set the Sender with a DISPLAY NAME
            String fromEmail = environment.getProperty("spring.mail.username");
            String displayName = "Micromart"; // 👈 This is the text users will see!

            helper.setFrom(fromEmail, displayName);
            helper.setTo(to);
            helper.setSubject(subject);

            // 4. Set Content (true = send as HTML if you want, false = plain text)
            helper.setText(content, false);

            // 5. Send
            javaMailSender.send(message);
            logger.info("✅ Email sent successfully to {}", to);

        } catch (Exception e) {
            logger.error("❌ Failed to send email to {}: {}", to, e.getMessage());
        }
    }
}