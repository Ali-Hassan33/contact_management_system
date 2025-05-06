package com.contact_management_system.controllers;

import com.contact_management_system.services.AuthService;
import com.nimbusds.jose.JOSEException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(GithubOAuthController.class)
@Import(GithubOAuthControllerTests.TestSecurityConfig.class)
class GithubOAuthControllerTests {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    AuthService authService;

    @Test
    @DisplayName("Should successfully authenticate with GitHub OAuth and return JWT token")
    @WithMockUser(username = "github_user@example.com")
    void testLogin() throws Exception {
        String jwtToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkdpdEh1YiBVc2VyIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";
        when(authService.login(any(Authentication.class))).thenReturn(jwtToken);

        mockMvc.perform(post("/github/oauth/login"))
                .andExpect(status().isOk())
                .andExpect(content().string(jwtToken));

        verify(authService).login(any(Authentication.class));
    }

    @Test
    @DisplayName("Should return forbidden status when login attempted without authentication")
    void testLoginWithoutAuthentication() throws Exception {
        mockMvc.perform(post("/github/oauth/login"))
                .andExpect(status().isForbidden());
    }

    static class TestSecurityConfig {

        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
            http.csrf(AbstractHttpConfigurer::disable)
                    .authorizeHttpRequests(auth -> auth
                            .requestMatchers("/github/oauth/login").authenticated()
                            .anyRequest().authenticated()
                    );
            return http.build();
        }
    }
}