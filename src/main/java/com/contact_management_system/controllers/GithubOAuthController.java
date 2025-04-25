package com.contact_management_system.controllers;

import com.contact_management_system.services.AuthService;
import com.nimbusds.jose.JOSEException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/github/oauth")
public class GithubOAuthController {

    private final AuthService authService;

    public GithubOAuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    ResponseEntity<String> login(Authentication authentication) throws JOSEException {
        return ResponseEntity.ok(authService.login(authentication));
    }
}
