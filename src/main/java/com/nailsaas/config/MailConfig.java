package com.nailsaas.config;

import com.nailsaas.util.ConfigReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Map;
import java.util.Properties;

@Configuration
public class MailConfig {

    @Value("${mail.config.path}")
    private String configPath;

    @Bean
    public JavaMailSender javaMailSender() {

        Map<String, String> config = ConfigReader.readConfig(configPath);

        String username = config.get("username");
        String password = config.get("password");

        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();

        // SMTP 設定
        mailSender.setHost("smtp.gmail.com");
        mailSender.setPort(587);

        mailSender.setUsername(username);
        mailSender.setPassword(password);

        // TLS 設定
        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        return mailSender;
    }
    
    @Bean
    public String mailSenderUsername() {
        Map<String, String> config = ConfigReader.readConfig(configPath);
        return config.get("username");
    }
}