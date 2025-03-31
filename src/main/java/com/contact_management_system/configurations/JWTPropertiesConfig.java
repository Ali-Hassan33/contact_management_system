package com.contact_management_system.configurations;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.OctetSequenceKey;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.time.DurationMin;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import javax.crypto.SecretKey;
import java.time.Duration;

@Getter
@Setter
@Validated
@ConfigurationProperties(prefix = "app.jwt")
public class JWTPropertiesConfig {

    @NotNull
    private SecretKey key;

    @NotNull
    private String issuer;

    @NotNull
    private JWSAlgorithm algorithm;

    @DurationMin(seconds = 1)
    private Duration expiresIn;

    public void setAlgorithm(String algorithm) {
        this.algorithm = JWSAlgorithm.parse(algorithm);
    }

    public void setKey(String key) {
        this.key = new OctetSequenceKey.Builder(key.getBytes())
                        .algorithm(algorithm)
                        .build()
                        .toSecretKey();
    }
}
