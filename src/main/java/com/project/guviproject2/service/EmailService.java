package com.project.guviproject2.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

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
        if (!mailEnabled) {
            return;
        }
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromAddress);
        message.setTo(to);
        message.setSubject("Welcome to Quiz Platform");
        message.setText("Your account is ready. You can now log in and start assessments.");
        mailSender.send(message);
    }

    public void sendResultEmail(String to, String quizTitle, int score, int total) {
        if (!mailEnabled) {
            return;
        }
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromAddress);
        message.setTo(to);
        message.setSubject("Quiz Result: " + quizTitle);
        message.setText("You completed \"" + quizTitle + "\".\nScore: " + score + "/" + total);
        mailSender.send(message);
    }
}
