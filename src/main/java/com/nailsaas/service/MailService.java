package com.nailsaas.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailService {

    private final JavaMailSender mailSender;

    private final String mailSenderUsername;

    MailService(String mailSenderUsername, JavaMailSender mailSender) {
        this.mailSenderUsername = mailSenderUsername;
        this.mailSender = mailSender;
    }

    public void sendMail(String to, String subject, String text) {

        SimpleMailMessage message = new SimpleMailMessage();

        // 使用設定檔的寄件人
        message.setFrom(mailSenderUsername);

        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);

        mailSender.send(message);
    }
}