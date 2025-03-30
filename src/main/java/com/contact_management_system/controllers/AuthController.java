package com.contact_management_system.controllers;

import com.contact_management_system.dtos.UserDto;
import com.contact_management_system.entities.User;
import com.contact_management_system.exceptions.EmailNotFoundException;
import com.contact_management_system.repositories.UserRepository;
import com.contact_management_system.services.AuthService;
import com.contact_management_system.services.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final JwtService jwtService;
    private final AuthService authService;
    private final UserRepository userRepository;

    public AuthController(JwtService jwtService, UserRepository userRepository, AuthService authService) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.authService = authService;
    }

    @PostMapping("/signUp")
    public ResponseEntity<User> signUp(@RequestBody UserDto user) {
        return ResponseEntity.ok(authService.save(user));
    }

    @PostMapping("/signIn")
    public ResponseEntity<String> signIn(Authentication authentication) {
        String name, email;
        Optional<OAuth2AuthenticationToken> oauth2 = Optional.of(authentication)
                .filter(OAuth2AuthenticationToken.class::isInstance)
                .map(OAuth2AuthenticationToken.class::cast);
        if (oauth2.isPresent()) {
            OAuth2User oAuth2User = oauth2.get().getPrincipal();
            email = Objects.requireNonNull(oAuth2User.getAttribute("email")).toString();
            if (!userRepository.existsByEmail(email)) {
                name = oAuth2User.getAttribute("name");
                signUp(UserDto.builder().name(name).email(email).build());
            }
        } else email = String.valueOf(authentication.getPrincipal());

        User user = userRepository.findByEmail(email).orElseThrow();
        Map<String, Object> claims = Map.of("id", user.getId(), "name", user.getUsername(), "email", email);
        return ResponseEntity.ok(jwtService.generateJwt(claims));
    }


//    @GetMapping("/")
//    public ResponseEntity<String> getToken(@RequestBody Map<String, Object> claims) {
//        return ResponseEntity.ok(jwtService.generateJwt(claims));
//    }
}
