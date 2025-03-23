package com.contact_management_system.controllers;

import com.contact_management_system.dtos.UserDto;
import com.contact_management_system.entities.User;
import com.contact_management_system.services.AuthService;
import com.contact_management_system.services.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {

    private final JwtService jwtService;
    private final AuthService authService;

    public AuthController(JwtService jwtService, AuthService authService) {
        this.jwtService = jwtService;
        this.authService = authService;
    }

    @PostMapping("/signUp")
    public ResponseEntity<User> signUp(@RequestBody UserDto user) {
        return ResponseEntity.ok(authService.save(user));
    }

    @GetMapping("/")
    public ResponseEntity<String> getToken(@RequestBody Map<String, Object> claims) {
        return ResponseEntity.ok(jwtService.generateJwt(claims));
    }
}
