package com.contact_management_system.services;

import com.contact_management_system.configurations.JWTPropertiesConfig;
import com.contact_management_system.entities.User;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.stereotype.Service;

import java.util.Map;

import static java.time.Instant.now;
import static java.util.Date.from;

@Service
public class JWTService {

    private final JWTPropertiesConfig jwtProperties;

    public JWTService(JWTPropertiesConfig jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    public String serializedJwt(User user) throws JOSEException {
        var jwt = new SignedJWT(header(), claimSet(Map.of("id", user.getId(), "name", user.getUsername(), "email", user.getEmail())));
        jwt.sign(new MACSigner(jwtProperties.getKey()));
        return jwt.serialize();
    }

    private JWSHeader header() {
        return new JWSHeader.Builder(jwtProperties.getAlgorithm()).build();
    }

    private JWTClaimsSet claimSet(Map<String, Object> claims) {
        var claimSetBuilder = new JWTClaimsSet.Builder()
                .issuer(jwtProperties.getIssuer())
                .issueTime(from(now()))
                .expirationTime(from(now().plus(jwtProperties.getExpiresIn())));

        claims.forEach(claimSetBuilder::claim);

        return claimSetBuilder.build();
    }
}
