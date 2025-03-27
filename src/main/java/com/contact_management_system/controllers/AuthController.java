package com.contact_management_system.controllers;

import com.contact_management_system.dtos.UserDto;
import com.contact_management_system.entities.User;
import com.contact_management_system.repositories.UserRepository;
import com.contact_management_system.services.AuthService;
import com.contact_management_system.services.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

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
        System.out.println("signIn()..."+ authentication);
        var email = String.valueOf(authentication.getPrincipal());
        User user = userRepository.findByEmail(email).orElseThrow();
        Map<String, Object> claims = Map.of("id", user.getId(), "name", user.getUsername(), "email", email);
        return ResponseEntity.ok(jwtService.generateJwt(claims));
    }


    @GetMapping("/")
    public ResponseEntity<String> getToken(@RequestBody Map<String, Object> claims) {
        return ResponseEntity.ok(jwtService.generateJwt(claims));
    }
}
