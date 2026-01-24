package com.micromart.notification.channels;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class EmailNotificationChannel implements NotificationChannel {

    private static final Logger logger = LoggerFactory.getLogger(EmailNotificationChannel.class);

    private final JavaMailSender javaMailSender;
    private final Environment environment;
    private final SpringTemplateEngine templateEngine;
    @Value("${app.frontend.url:http://127.0.0.1:5173}")
    private String frontendUrl;

    @Override
    public void sendNotification(String to, String subject, String content) {
        sendSimpleHtmlEmail(to, subject, content);
    }

    public void sendHtmlVerificationEmail(String to, String userName, String token) {
        logger.info("📧 Preparing Verification Email for: {}", to);

        try {
            Context context = new Context();

            String safeName = (userName != null && !userName.isEmpty()) ? userName : "Friend";
            context.setVariable("userName", safeName);
            String link = frontendUrl + "/verify?token=" + token;
            context.setVariable("verificationLink", link);
            String htmlBody = templateEngine.process("verification-email", context);
            sendSimpleHtmlEmail(to, "🚀 Verify your Micromart Account", htmlBody);
            logger.info("✅ Verification Email sent to {} (User: {})", to, safeName);

        } catch (Exception e) {
            logger.error("❌ Failed to create verification email: {}", e.getMessage());
        }
    }
    private void sendSimpleHtmlEmail(String to, String subject, String htmlContent) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, StandardCharsets.UTF_8.name());

            String fromEmail = environment.getProperty("spring.mail.username");
            String displayName = "Micromart";
            helper.setFrom(fromEmail, displayName);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);

            javaMailSender.send(message);

        } catch (Exception e) {
            logger.error("❌ Failed to send email to {}: {}", to, e.getMessage());
            throw new RuntimeException("Email sending failed", e);
        }
    }
}