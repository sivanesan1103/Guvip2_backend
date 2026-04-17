package com.project.guviproject2.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.project.guviproject2.exception.BadRequestException;

@Service
public class EmailService {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;
    private final String fromAddress;
    private final boolean mailEnabled;

    public EmailService(JavaMailSender mailSender,
                        @Value("${spring.mail.username}") String fromAddress,
                        @Value("${app.mail.enabled:false}") boolean mailEnabled) {
        this.mailSender = mailSender;
        this.fromAddress = fromAddress;
        this.mailEnabled = mailEnabled;
    }

    public void sendRegistrationEmail(String to) {
        sendMailIfEnabled(to, "Welcome to Quiz Platform",
                "Your account is ready. You can now log in and start assessments.");
    }

    public void sendResultEmail(String to, String quizTitle, int score, int total) {
        sendMailIfEnabled(to, "Quiz Result: " + quizTitle,
                "You completed \"" + quizTitle + "\".\nScore: " + score + "/" + total);
    }

    public void sendTestEmail(String to, String subject, String body) {
        if (!mailEnabled) {
            throw new BadRequestException("Email is disabled. Set app.mail.enabled=true to send emails.");
        }
        validateFromAddress();
        try {
            sendMail(to, subject, body);
        } catch (MailAuthenticationException ex) {
            throw new BadRequestException("SMTP authentication failed. Verify spring.mail.username and app password.");
        } catch (MailException ex) {
            throw new BadRequestException("Unable to send email. Check SMTP configuration and network access.");
        } catch (IllegalArgumentException ex) {
            throw new BadRequestException("Invalid mail configuration. Set MAIL_USERNAME to a valid sender address.");
        }
    }

    private void sendMailIfEnabled(String to, String subject, String body) {
        if (!mailEnabled) {
            return;
        }
        if (fromAddress == null || fromAddress.isBlank()) {
            LOGGER.warn("Skipping non-critical email to {} because MAIL_USERNAME is not configured.", to);
            return;
        }
        try {
            sendMail(to, subject, body);
        } catch (MailException | IllegalArgumentException ex) {
            LOGGER.warn("Skipping non-critical email to {} due to SMTP issue: {}", to, ex.getMessage());
        }
    }

    private void sendMail(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromAddress);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        mailSender.send(message);
    }

    private void validateFromAddress() {
        if (fromAddress == null || fromAddress.isBlank()) {
            throw new BadRequestException("MAIL_USERNAME is required when email is enabled.");
        }
    }
}
