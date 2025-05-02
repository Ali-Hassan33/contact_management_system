package com.contact_management_system.configurations;

import com.contact_management_system.security.providers.UserAuthenticationProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    @Bean
    @Order(1)
    SecurityFilterChain apiSecurityFilterChain(HttpSecurity http,
                                               final CorsConfigurationSource corsConfigurationSource) throws Exception {
        return http
                .securityMatcher("/api/**")
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(withDefaults()))
                .authorizeHttpRequests(request -> request.anyRequest().authenticated())
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .csrf(AbstractHttpConfigurer::disable)
                .build();
    }

    @Bean
    @Order(2)
    SecurityFilterChain securityFilterChain(HttpSecurity http, final CorsConfigurationSource corsConfigurationSource,
                                            UserAuthenticationProvider userAuthenticationProvider) throws Exception {
        return http
                .securityMatcher("/auth/**")
                .httpBasic(withDefaults())
                .authorizeHttpRequests(request -> request.requestMatchers("/auth/signup")
                        .permitAll()
                        .anyRequest()
                        .authenticated())
                .authenticationProvider(userAuthenticationProvider)
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .csrf(AbstractHttpConfigurer::disable)
                .build();
    }

    @Bean
    @Order(3)
    SecurityFilterChain githubSecurityFilterChain(HttpSecurity http, final CorsConfigurationSource corsConfigurationSource) throws Exception {
        return http
                .oauth2Login(oauth2 -> oauth2.defaultSuccessUrl("http://localhost:3000/github", true))
                .authorizeHttpRequests(request -> request.requestMatchers("/github/oauth/login")
                        .authenticated()
                        .anyRequest()
                        .permitAll())
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .csrf(AbstractHttpConfigurer::disable)
                .build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        var corsConfiguration = new CorsConfiguration();
        corsConfiguration.addAllowedOrigin("http://localhost:3000");
        corsConfiguration.addAllowedHeader("*");
        corsConfiguration.setAllowedMethods(List.of("GET", "POST", "DELETE", "PUT", "OPTIONS"));
        corsConfiguration.setAllowCredentials(true);
        var source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);
        return source;
    }

    @Bean
    JwtDecoder jwtDecoder(JWTPropertiesConfig jwtPropertiesConfig) {
        return NimbusJwtDecoder
                .withSecretKey(jwtPropertiesConfig.getKey())
                .build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
