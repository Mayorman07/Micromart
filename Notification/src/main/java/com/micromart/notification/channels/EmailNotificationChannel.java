package com.micromart.notification.channels;

import jakarta.mail.MessagingException;
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

    public EmailNotificationChannel(JavaMailSender javaMailSender,
                                    Environment environment,
                                    SpringTemplateEngine templateEngine) {
        this.javaMailSender = javaMailSender;
        this.environment = environment;
        this.templateEngine = templateEngine;
    }

    public void sendHtmlVerificationEmail(String to, String userName, String token) {
        logger.info("📧 Preparing Verification Email for: {}", to);

        try {
            Context context = new Context();

            String safeName = (userName != null && !userName.isEmpty()) ? userName : "Partner";
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

    public void sendHtmlPasswordResetEmail(String to, String userName, String token) {
        logger.info("🔑 Preparing Password Reset Email for: {}", to);

        try {
            Context context = new Context();
            context.setVariable("userName", userName);

            String link = frontendUrl + "/reset-password?token=" + token;
            context.setVariable("resetLink", link);

            String htmlBody = templateEngine.process("password-reset", context);
            sendSimpleHtmlEmail(to, "🔒 Reset Your Micromart Password", htmlBody);

            logger.info("✅ Password Reset Email sent to {}", to);

        } catch (Exception e) {
            logger.error("❌ Failed to create password reset email: {}", e.getMessage());
        }
    }

    public void sendHtmlEmail(String to, String subject, String templateName, Context context) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            String htmlContent = templateEngine.process(templateName, context);

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);
            javaMailSender.send(mimeMessage);
            logger.info("✅ Generic HTML Email sent to {}", to);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send email", e);
        }
   }
}