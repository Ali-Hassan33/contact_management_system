package com.contact_management_system.services;

import com.contact_management_system.configurations.JwtPropertiesConfig;
import com.contact_management_system.exceptions.JwtException;
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
public class JwtService {

    private final JwtPropertiesConfig jwtProperties;

    public JwtService(JwtPropertiesConfig jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    public String generateJwt(Map<String, Object> claims) {
        var jwt = new SignedJWT(header(), claimSet(claims));

        try {
            var signer = new MACSigner(jwtProperties.getKey());
            jwt.sign(signer);
        } catch (JOSEException e) {
            throw new JwtException(e);
        }
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
