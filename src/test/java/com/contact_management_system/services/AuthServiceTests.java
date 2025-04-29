package com.contact_management_system.services;

import com.contact_management_system.dtos.UserDto;
import com.contact_management_system.entities.User;
import com.nimbusds.jose.JOSEException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTests {

    @Mock
    private UserService userService;

    @Mock
    private JWTService jwtService;

    @Mock
    private Authentication authentication;

    @Mock
    private OAuth2AuthenticationToken oAuth2AuthenticationToken;

    @InjectMocks
    private AuthService authService;

    @Test
    @DisplayName("Should sign up a user successfully")
    void testSignUp() {
        // Arrange
        UserDto userDto = UserDto.builder()
                .name("Test User")
                .email("test@example.com")
                .password("password123")
                .build();
        
        User expectedUser = new User("Test User", "test@example.com", "encodedPassword");
        when(userService.saveBasicAuthUser(any(UserDto.class))).thenReturn(expectedUser);
        
        // Act
        User result = authService.signUp(userDto);
        
        // Assert
        assertNotNull(result);
        assertEquals(expectedUser, result);
        verify(userService).saveBasicAuthUser(userDto);
    }

    @Test
    @DisplayName("Should login with basic authentication")
    void testLoginWithBasicAuth() throws JOSEException {
        // Arrange
        String email = "test@example.com";
        User user = new User("Test User", email, "encodedPassword");
        String expectedToken = "jwt.token.string";
        
        when(authentication.getPrincipal()).thenReturn(email);
        when(userService.getUserByEmail(email)).thenReturn(user);
        when(jwtService.signJWT(user)).thenReturn(expectedToken);
        
        // Act
        String result = authService.login(authentication);
        
        // Assert
        assertEquals(expectedToken, result);
        verify(userService).getUserByEmail(email);
        verify(jwtService).signJWT(user);
    }

    @Test
    @DisplayName("Should login with OAuth2 for existing user")
    void testLoginWithOAuth2ExistingUser() throws JOSEException {
        // Arrange
        String email = "oauth@example.com";
        User user = new User("OAuth User", email);
        String expectedToken = "oauth.jwt.token";
        
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("email", email);
        attributes.put("name", "OAuth User");
        
        OAuth2User oAuth2User = new DefaultOAuth2User(
                null, attributes, "email");
        
        when(oAuth2AuthenticationToken.getPrincipal()).thenReturn(oAuth2User);
        when(userService.isUserExist(email)).thenReturn(true);
        when(userService.getUserByEmail(email)).thenReturn(user);
        when(jwtService.signJWT(user)).thenReturn(expectedToken);
        
        // Act
        String result = authService.login(oAuth2AuthenticationToken);
        
        // Assert
        assertEquals(expectedToken, result);
        verify(userService).isUserExist(email);
        verify(userService).getUserByEmail(email);
        verify(userService, never()).saveOAuth2User(any());
        verify(jwtService).signJWT(user);
    }

    @Test
    @DisplayName("Should login with OAuth2 for new user")
    void testLoginWithOAuth2NewUser() throws JOSEException {
        // Arrange
        String email = "newoauth@example.com";
        String name = "New OAuth User";
        User newUser = new User(name, email);
        String expectedToken = "new.oauth.jwt.token";
        
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("email", email);
        attributes.put("name", name);
        
        OAuth2User oAuth2User = new DefaultOAuth2User(
                null, attributes, "email");
        
        when(oAuth2AuthenticationToken.getPrincipal()).thenReturn(oAuth2User);
        when(userService.isUserExist(email)).thenReturn(false);
        when(userService.saveOAuth2User(any(UserDto.class))).thenReturn(newUser);
        when(jwtService.signJWT(newUser)).thenReturn(expectedToken);
        
        // Act
        String result = authService.login(oAuth2AuthenticationToken);
        
        // Assert
        assertEquals(expectedToken, result);
        verify(userService).isUserExist(email);
        verify(userService, never()).getUserByEmail(anyString());
        verify(userService).saveOAuth2User(any(UserDto.class));
        verify(jwtService).signJWT(newUser);
    }
}