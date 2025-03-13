package com.contact_management_system.controllers;

import com.contact_management_system.services.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final JwtService jwtService;

    public AuthController(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @GetMapping("/token")
    public ResponseEntity<String> getToken(@RequestBody Map<String, Object> claims) {
        return ResponseEntity.ok(jwtService.generateJwt(claims));
    }
}
