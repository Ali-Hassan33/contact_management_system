package com.contact_management_system.controllers;

import com.contact_management_system.dtos.ResetPasswordDto;
import com.contact_management_system.services.MimeService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MimeController.class)
@Import(MimeControllerTests.TestSecurityConfig.class)
class MimeControllerTests {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    MimeService mimeService;

    @Test
    @DisplayName("Should send password reset email to the specified recipient")
    @WithMockUser
    void testResetPasswordMail() throws Exception {
        String recipientEmail = "test@example.com";
        doNothing().when(mimeService).sendEmail(eq(recipientEmail));

        mockMvc.perform(post("/mime/password/reset/{recipientEmail}", recipientEmail))
                .andExpect(status().isOk());

        verify(mimeService).sendEmail(eq(recipientEmail));
    }

    @Test
    @DisplayName("Should reset password with valid token and new password")
    @WithMockUser
    void testResetPassword() throws Exception {
        String requestBody = """
                {
                    "token":"123e4567-e89b-12d3-a456-426614174000",
                    "newPassword":"newPassword123"
                }
                """;
        doNothing().when(mimeService).resetPassword(any(ResetPasswordDto.class));

        mockMvc.perform(post("/mime/password/reset")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk());

        verify(mimeService).resetPassword(any(ResetPasswordDto.class));
    }


    static class TestSecurityConfig {
        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
            http.csrf(AbstractHttpConfigurer::disable);
            return http.build();
        }
    }
}
