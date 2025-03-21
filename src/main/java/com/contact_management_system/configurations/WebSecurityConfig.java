package com.contact_management_system.configurations;

import com.contact_management_system.security.providers.UserAuthenticationProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.security.Security;
import java.util.List;

import static org.springframework.security.config.Customizer.withDefaults;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    @Bean
    @Order(1)
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .securityMatcher("/auth/**")
                .csrf(AbstractHttpConfigurer::disable)
//                .oauth2Login(oauth2 -> oauth2.defaultSuccessUrl("http://localhost:3000", true))
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(withDefaults()))
                .authorizeHttpRequests(request -> request.requestMatchers("/auth/signUp", "/auth/")
                        .permitAll()
                        .anyRequest()
                        .authenticated())
//                .httpBasic(withDefaults())
//                .authenticationProvider(userAuthenticationProvider)
                .build();
    }

    @Bean
    @Order(2)
    SecurityFilterChain apisecurityFilterChain(HttpSecurity http, UserAuthenticationProvider userAuthenticationProvider) throws Exception {
        return http
                .securityMatcher("/api/**")
                .csrf(AbstractHttpConfigurer::disable)
                .cors(withDefaults())
                .oauth2Login(oauth2 -> oauth2.defaultSuccessUrl("http://localhost:3000", true))
                .authorizeHttpRequests(request -> request
//                        .requestMatchers("/api/contacts")
//                        .permitAll()
                        .anyRequest()
                        .authenticated())
                .httpBasic(withDefaults())
                .sessionManagement(session -> session.sessionCreationPolicy(STATELESS))
                .authenticationProvider(userAuthenticationProvider)
                .build();
    }

    @Bean
    CorsConfiguration corsConfiguration() {
        var  corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowedOrigins(List.of("http://localhost:3000", "http://localhost:8080"));
        corsConfiguration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE"));
        corsConfiguration.setAllowCredentials(true);
        corsConfiguration.addAllowedHeader("*");
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);
        return corsConfiguration;
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    JwtDecoder jwtDecoder(JwtPropertiesConfig jwtPropertiesConfig) {
        return NimbusJwtDecoder
                .withSecretKey(jwtPropertiesConfig.getKey())
                .build();
    }
}
