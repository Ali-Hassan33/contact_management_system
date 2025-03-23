package com.contact_management_system.controllers;

import com.contact_management_system.entities.ContactProfile;
import com.contact_management_system.entities.User;
import com.contact_management_system.repositories.ContactProfileRepository;
import com.contact_management_system.repositories.UserRepository;
import com.contact_management_system.services.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class UserController {

    private final ContactProfileRepository contactProfileRepository;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    public UserController(ContactProfileRepository contactProfileRepository,
                          UserRepository userRepository, JwtService jwtService) {
        this.contactProfileRepository = contactProfileRepository;
        this.userRepository = userRepository;
        this.jwtService = jwtService;
    }

    @GetMapping("/contacts")
    public Iterable<ContactProfile> contacts(Authentication authentication) {
        System.out.println(authentication);
        return contactProfileRepository.findAllByUserId(30L);
    }

    @PostMapping("/signIn")
    public ResponseEntity<String> signIn(Authentication authentication) {
        var email = String.valueOf(authentication.getPrincipal());
        User user = userRepository.findByEmail(email).orElseThrow();
        Map<String, Object> claims = Map.of("id", user.getId(), "name", user.getUsername(), "email", email);
        return ResponseEntity.ok(jwtService.generateJwt(claims));
    }

    @PostMapping("/contact/save")
    public ResponseEntity<ContactProfile> save(@RequestBody ContactProfile contactProfile) {
        contactProfile.setUser(userRepository.findById(10L).orElseThrow());
        contactProfile.getPhoneNumbers().forEach(phoneNumber -> phoneNumber.setContactProfile(contactProfile));
        contactProfile.getEmailAddresses().forEach(emailAddress -> emailAddress.setContactProfile(contactProfile));
        return ResponseEntity.ok(contactProfileRepository.save(contactProfile));
    }

    @DeleteMapping("/contact/{id}")
    public void removeContact(@PathVariable Long id) {
        contactProfileRepository.deleteById(id);
    }
}

