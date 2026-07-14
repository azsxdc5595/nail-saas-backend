package com.nailsaas.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailService {

    @Autowired
    private JavaMailSender mailSender;

    // 注入寄件人
    @Autowired
    private String mailSenderUsername;

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