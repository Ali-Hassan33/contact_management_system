package com.contact_management_system.controllers;

import com.contact_management_system.dtos.UserDto;
import com.contact_management_system.entities.User;
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
    private final AuthService userService;

    public AuthController(JwtService jwtService, AuthService userService) {
        this.jwtService = jwtService;
        this.userService = userService;
    }

    @PostMapping("/signUp")
    public ResponseEntity<User> signUp(@RequestBody UserDto user) {
        return ResponseEntity.ok(userService.save(user));
    }

    @GetMapping("/")
    public ResponseEntity<String> getToken(@RequestBody Map<String, Object> claims) {
        return ResponseEntity.ok(jwtService.generateJwt(claims));
    }

    @GetMapping("/greet")
    public String greet(Authentication authentication) {
        System.out.println(authentication);
        return "Hello";
    }
}
