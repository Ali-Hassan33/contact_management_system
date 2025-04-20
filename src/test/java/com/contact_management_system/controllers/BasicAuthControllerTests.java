package com.contact_management_system.controllers;

import com.contact_management_system.dtos.UserDto;
import com.contact_management_system.entities.User;
import com.contact_management_system.services.AuthService;
import com.nimbusds.jose.JOSEException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BasicAuthController.class)
@Import(BasicAuthControllerTests.TestSecurityConfig.class)
class BasicAuthControllerTests {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    AuthService authService;

    @Test
    void testSignup() throws Exception {
        String requestBody = """
                {
                    "name": "Test User",
                    "email": "test@example.com",
                    "password": "password123"
                }
                """;

        User mockUser = User.builder()
                .id(1L)
                .username("Test User")
                .email("test@example.com")
                .password("encoded_password")
                .build();

        when(authService.signUp(any(UserDto.class))).thenReturn(mockUser);

        mockMvc.perform(post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.username", is("Test User")))
                .andExpect(jsonPath("$.email", is("test@example.com")));

        verify(authService).signUp(any(UserDto.class));
    }

    @Test
    @WithMockUser(username = "test@example.com")
    void testLogin() throws Exception {
        String jwtToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IlRlc3QgVXNlciIsImlhdCI6MTUxNjIzOTAyMn0.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";
        when(authService.login(any(Authentication.class))).thenReturn(jwtToken);

        mockMvc.perform(post("/auth/login"))
                .andExpect(status().isOk())
                .andExpect(content().string(jwtToken));

        verify(authService).login(any(Authentication.class));
    }

    @Test
    void testLoginWithoutAuthentication() throws Exception {
        mockMvc.perform(post("/auth/login"))
                .andExpect(status().isForbidden());
    }

    @Test
    void testSignupWithInvalidData() throws Exception {
        String requestBody = """
                {
                    "email": "test@example.com"
                }
                """;

        when(authService.signUp(any(UserDto.class))).thenReturn(null);

        mockMvc.perform(post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").doesNotExist());
    }

    @Test
    void testSignupWithInvalidEmail() {
        String requestBody = """
                {
                    "name": "Test User",
                    "email": "invalid-email",
                    "password": "password123"
                }
                """;

        when(authService.signUp(any(UserDto.class))).thenThrow(new IllegalArgumentException("Invalid email format"));

        boolean exceptionThrown = false;
        try {
            mockMvc.perform(post("/auth/signup")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody));
        } catch (Exception e) {
            exceptionThrown = true;
        }

        assertTrue(exceptionThrown, "Expected exception was not thrown");
    }

    @Test
    @WithMockUser(username = "test@example.com")
    void testLoginWithJoseException() throws Exception {
        when(authService.login(any(Authentication.class))).thenThrow(new JOSEException("Error creating JWT"));

        boolean exceptionThrown = false;
        try {
            mockMvc.perform(post("/auth/login"));
        } catch (Exception e) {
            exceptionThrown = true;
        }

        assertTrue(exceptionThrown, "Expected exception was not thrown");
    }

    static class TestSecurityConfig {

        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
            http.csrf(AbstractHttpConfigurer::disable)
                    .authorizeHttpRequests(auth -> auth
                            .requestMatchers("/auth/signup").permitAll()
                            .requestMatchers("/auth/login").authenticated()
                            .anyRequest().authenticated()
                    );
            return http.build();
        }
    }
}
