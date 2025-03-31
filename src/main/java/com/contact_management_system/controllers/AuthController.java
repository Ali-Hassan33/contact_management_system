package com.contact_management_system.controllers;

import com.contact_management_system.dtos.UserDto;
import com.contact_management_system.entities.User;
import com.contact_management_system.services.AuthService;
import com.nimbusds.jose.JOSEException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/signUp")
    public ResponseEntity<User> signUp(@RequestBody UserDto user) {
        return ResponseEntity.ok(authService.signUp(user));
    }

    @PostMapping("/signIn")
    public ResponseEntity<String> signIn(Authentication authentication) throws JOSEException {
        return ResponseEntity.ok(authService.login(authentication));
    }


}
