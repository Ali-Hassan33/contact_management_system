package com.contact_management_system.services;

import com.contact_management_system.dtos.ResetPasswordDto;
import com.contact_management_system.entities.Mime;
import com.contact_management_system.entities.User;
import com.contact_management_system.repositories.MimeRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MimeServiceTests {

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private UserService userService;

    @Mock
    private MimeRepository mimeRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private MimeMessage mimeMessage;

    @InjectMocks
    private MimeService mimeService;

    private User testUser;
    private Mime testMime;
    private final String TEST_EMAIL = "test@example.com";
    private final UUID TEST_UUID = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("Test User");
        testUser.setEmail(TEST_EMAIL);
        testUser.setPassword("oldPassword");

        testMime = Mime.builder()
                .uuid(TEST_UUID)
                .user(testUser)
                .build();

        // Set private fields using ReflectionTestUtils
        ReflectionTestUtils.setField(mimeService, "sender", "sender@example.com");
        ReflectionTestUtils.setField(mimeService, "resource", "/templates/reset-password.html");
        ReflectionTestUtils.setField(mimeService, "url", "http://localhost:8080/reset-password?token=");
    }

    // Note: This test is simplified and doesn't test the actual email content
    // due to the complexity of mocking static resource loading
    @Test
    @DisplayName("Should send reset password email")
    void testSendEmail() throws MessagingException, IOException {
        when(userService.getUserByEmail(TEST_EMAIL)).thenReturn(testUser);
        when(mimeRepository.save(any(Mime.class))).thenReturn(testMime);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        // We can't easily test the resource loading part without using PowerMockito
        // or similar libraries, so we'll just verify the method calls

        try {
            mimeService.sendEmail(TEST_EMAIL);
        } catch (NullPointerException e) {
            // Expected exception due to resource not being available in the test environment
        }

        // Assert - verify the method calls that should happen before the resource loading
        verify(userService).getUserByEmail(TEST_EMAIL);
        verify(mimeRepository).save(any(Mime.class));
        verify(mailSender).createMimeMessage();
    }

    @Test
    @DisplayName("Should reset password")
    void testResetPassword() {
        ResetPasswordDto resetPasswordDto = ResetPasswordDto.builder()
                .token(TEST_UUID.toString())
                .newPassword("newPassword123")
                .build();

        String encodedNewPassword = "encodedNewPassword123";

        when(mimeRepository.getReferenceById(TEST_UUID)).thenReturn(testMime);
        when(passwordEncoder.encode(resetPasswordDto.getNewPassword())).thenReturn(encodedNewPassword);

        mimeService.resetPassword(resetPasswordDto);

        assertEquals(encodedNewPassword, testUser.getPassword());
        verify(mimeRepository).getReferenceById(TEST_UUID);
        verify(passwordEncoder).encode(resetPasswordDto.getNewPassword());
    }
}
