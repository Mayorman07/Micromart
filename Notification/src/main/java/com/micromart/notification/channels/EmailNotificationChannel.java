package com.micromart.notification.channels;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.nio.charset.StandardCharsets;

/**
 * Handles the construction and delivery of email notifications.
 * Integrates with JavaMailSender for transport and Thymeleaf for HTML template processing.
 */
@Component
public class EmailNotificationChannel implements NotificationChannel {

    private static final Logger logger = LoggerFactory.getLogger(EmailNotificationChannel.class);

    private final JavaMailSender javaMailSender;
    private final Environment environment;
    private final SpringTemplateEngine templateEngine;

    /**
     * Constructs the EmailNotificationChannel with required dependencies.
     *
     * @param javaMailSender Spring's mail sender for dispatching MimeMessages.
     * @param environment    Application environment for accessing configuration properties.
     * @param templateEngine Thymeleaf engine for parsing email templates.
     */
    public EmailNotificationChannel(JavaMailSender javaMailSender,
                                    Environment environment,
                                    SpringTemplateEngine templateEngine) {
        this.javaMailSender = javaMailSender;
        this.environment = environment;
        this.templateEngine = templateEngine;
    }

    /**
     * Dispatches a raw HTML or text notification.
     * Satisfies the NotificationChannel interface contract.
     *
     * @param to      The recipient's email address.
     * @param subject The subject line of the email.
     * @param content The raw string content (can be HTML) to send.
     */
    @Override
    public void sendNotification(String to, String subject, String content) {
        sendSimpleHtmlEmail(to, subject, content);
    }

    /**
     * Processes and sends the account verification email template.
     *
     * @param to       The new user's email address.
     * @param userName The user's display name (defaults to "Partner" if null).
     * @param token    The unique verification token to append to the frontend URL.
     */
    public void sendHtmlVerificationEmail(String to, String userName, String token) {
        logger.info("Preparing Verification Email for: {}", to);

        try {
            Context context = new Context();
            String safeName = (userName != null && !userName.isEmpty()) ? userName : "Partner";
            context.setVariable("userName", safeName);

            String link = environment.getProperty("app.frontend.url") + "/verify?token=" + token;
            context.setVariable("verificationLink", link);

            String htmlBody = templateEngine.process("verification-email", context);
            sendSimpleHtmlEmail(to, "Verify your Micromart Account", htmlBody);

            logger.info("Verification Email sent to {} (User: {})", to, safeName);

        } catch (Exception e) {
            logger.error("Failed to create verification email: {}", e.getMessage());
        }
    }

    /**
     * Processes and sends the password reset email template.
     *
     * @param to       The user's email address requesting the reset.
     * @param userName The user's display name.
     * @param token    The secure password reset token.
     */
    public void sendHtmlPasswordResetEmail(String to, String userName, String token) {
        logger.info("Preparing Password Reset Email for: {}", to);

        try {
            Context context = new Context();
            context.setVariable("userName", userName);

            String link = environment.getProperty("app.frontend.url") + "/reset-password?token=" + token;
            context.setVariable("resetLink", link);

            String htmlBody = templateEngine.process("password-reset", context);
            sendSimpleHtmlEmail(to, "Reset Your Micromart Password", htmlBody);

            logger.info("Password Reset Email sent to {}", to);

        } catch (Exception e) {
            logger.error("Failed to create password reset email: {}", e.getMessage());
        }
    }

    /**
     * A flexible sender for custom Thymeleaf templates.
     * Allows passing a pre-configured context object to any specified template file.
     *
     * @param to           The recipient's email address.
     * @param subject      The email subject line.
     * @param templateName The name of the Thymeleaf HTML template file (without extension).
     * @param context      The Thymeleaf context containing variable mappings.
     */
    public void sendHtmlEmail(String to, String subject, String templateName, Context context) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, StandardCharsets.UTF_8.name());

            String htmlContent = templateEngine.process(templateName, context);

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);

            javaMailSender.send(mimeMessage);
            logger.info("Generic HTML Email sent to {}", to);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send generic HTML email", e);
        }
    }

    /**
     * Core utility to assemble and transmit the actual MimeMessage.
     *
     * @param to          The recipient's email address.
     * @param subject     The subject line.
     * @param htmlContent The fully processed HTML body.
     * @throws RuntimeException if the email dispatch fails.
     */
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
            logger.error("Failed to send email to {}: {}", to, e.getMessage());
            throw new RuntimeException("Email sending failed", e);
        }
    }

    public void sendPaymentSuccessEmail(String toEmail, String orderId) {
        try {
            Context context = new Context();
            context.setVariable("orderId", orderId);
            context.setVariable("trackUrl", environment.getProperty("app.frontend.url") + "/orders/" + orderId);

            String htmlBody = templateEngine.process("payment-success-email", context);
            sendSimpleHtmlEmail(toEmail, "Payment Received! Order #" + orderId, htmlBody);

            logger.info("Payment success email sent for order {}", orderId);
        } catch (Exception e) {
            logger.error("Error building payment success email: {}", e.getMessage());
        }
    }

    public void sendPaymentCancelledEmail(String toEmail, String orderId) {
        try {
            Context context = new Context();
            context.setVariable("orderId", orderId);
            context.setVariable("retryUrl", environment.getProperty("app.frontend.url") + "/checkout");

            String htmlBody = templateEngine.process("payment-cancelled-email", context);
            sendSimpleHtmlEmail(toEmail, "Order Cancelled - #" + orderId, htmlBody);

            logger.info("Payment cancellation email sent for order {}", orderId);
        } catch (Exception e) {
            logger.error("Error building payment cancelled email: {}", e.getMessage());
        }
    }
}