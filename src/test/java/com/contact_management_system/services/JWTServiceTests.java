package com.contact_management_system.services;

import com.contact_management_system.configurations.JWTPropertiesConfig;
import com.contact_management_system.entities.User;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.OctetSequenceKey;
import com.nimbusds.jwt.SignedJWT;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.text.ParseException;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JWTServiceTests {

    @Mock
    private JWTPropertiesConfig jwtProperties;

    @InjectMocks
    private JWTService jwtService;

    private User testUser;
    private final String TEST_ISSUER = "test-issuer";

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("Test User");
        testUser.setEmail("test@example.com");

        when(jwtProperties.getAlgorithm()).thenReturn(JWSAlgorithm.HS256);
        when(jwtProperties.getIssuer()).thenReturn(TEST_ISSUER);

        String TEST_KEY = "testsecretkeytestsecretkeytestsecretkey"; // 32+ bytes for HS256
        when(jwtProperties.getKey()).thenReturn(new OctetSequenceKey.Builder(TEST_KEY.getBytes())
                .algorithm(JWSAlgorithm.HS256)
                .build()
                .toSecretKey());
        when(jwtProperties.getExpiresIn()).thenReturn(Duration.ofHours(1));
    }

    @Test
    @DisplayName("Should create a valid JWT token for a user")
    void testSignJWT() throws JOSEException, ParseException {
        String jwtString = jwtService.signJWT(testUser);

        assertNotNull(jwtString);

        SignedJWT signedJWT = SignedJWT.parse(jwtString);

        assertEquals(JWSAlgorithm.HS256, signedJWT.getHeader().getAlgorithm());

        assertEquals(TEST_ISSUER, signedJWT.getJWTClaimsSet().getIssuer());
        assertEquals(1L, signedJWT.getJWTClaimsSet().getClaim("id"));
        assertEquals("Test User", signedJWT.getJWTClaimsSet().getClaim("name"));
        assertEquals("test@example.com", signedJWT.getJWTClaimsSet().getClaim("email"));

        assertNotNull(signedJWT.getJWTClaimsSet().getExpirationTime());

        assertNotNull(signedJWT.getJWTClaimsSet().getIssueTime());
    }

    @Test
    @DisplayName("Should throw NullPointerException when signing with null key")
    void testSignJWTWithInvalidKey() {
        when(jwtProperties.getKey()).thenReturn(null);

        assertThrows(NullPointerException.class, () -> jwtService.signJWT(testUser));
    }
}
