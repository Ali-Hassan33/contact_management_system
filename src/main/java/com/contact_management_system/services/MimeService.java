package com.contact_management_system.services;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;

import static java.nio.charset.StandardCharsets.UTF_8;

@Service
public class MimeService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String sender;

    @Value("${app.mime.reset-password.resource}")
    private String resource;

    @Value("${app.mime.reset-password.url}")
    private String url;

    public MimeService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendEmail(String to, String subject) throws MessagingException, IOException {
        String token = "???";
        MimeMessage mime = mailSender.createMimeMessage();

        MimeMessageHelper mimeHelper = new MimeMessageHelper(mime, true);
        mimeHelper.setFrom(new InternetAddress(sender));
        mimeHelper.setTo(to);
        mimeHelper.setSubject(subject);

        try (InputStream htmlContent = MimeService.class.getResourceAsStream(resource)) {
            assert htmlContent != null;
            String text = new String(htmlContent.readAllBytes(), UTF_8);
            mimeHelper.setText(text.replace("${reset-password}", url + token), true);
        }
        mailSender.send(mime);
    }
}
