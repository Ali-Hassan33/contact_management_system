package com.contact_management_system.services;

import com.contact_management_system.dtos.ResetPasswordDto;
import com.contact_management_system.entities.Mime;
import com.contact_management_system.repositories.MimeRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import static java.nio.charset.StandardCharsets.UTF_8;

@Service
public class MimeService {

    private final JavaMailSender mailSender;
    private final UserService userService;
    private final MimeRepository mimeRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${spring.mail.username}")
    private String sender;

    @Value("${app.mime.reset-password.resource}")
    private String resource;

    @Value("${app.mime.reset-password.url}")
    private String url;

    public MimeService(JavaMailSender mailSender, UserService userService, MimeRepository mimeRepository, PasswordEncoder passwordEncoder) {
        this.mailSender = mailSender;
        this.userService = userService;
        this.mimeRepository = mimeRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void sendEmail(String recipientEmail) throws MessagingException, IOException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();

        MimeMessageHelper mimeHelper = new MimeMessageHelper(mimeMessage, true);
        mimeHelper.setFrom(new InternetAddress(sender));
        mimeHelper.setTo(recipientEmail);
        mimeHelper.setSubject("Password Reset");

        try (InputStream htmlContent = MimeService.class.getResourceAsStream(resource)) {
            assert htmlContent != null;
            String text = new String(htmlContent.readAllBytes(), UTF_8);
            mimeHelper.setText(text.replace("${reset-password}",
                    url + mimeRepository.save(
                            Mime.builder()
                                    .user(userService.getUserByEmail(recipientEmail))
                                    .build()).getUuid()), true
            );
        }
        mailSender.send(mimeMessage);
    }

    @Transactional
    public void resetPassword(ResetPasswordDto resetPasswordDto) {
        mimeRepository.getReferenceById(UUID.fromString(resetPasswordDto.getToken()))
                .getUser()
                .setPassword(passwordEncoder.encode(resetPasswordDto.getNewPassword()));
    }
}
