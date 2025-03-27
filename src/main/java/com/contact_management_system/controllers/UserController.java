package com.contact_management_system.controllers;

import com.contact_management_system.entities.ContactProfile;
import com.contact_management_system.repositories.ContactProfileRepository;
import com.contact_management_system.repositories.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class UserController {

    private final ContactProfileRepository contactProfileRepository;
    private final UserRepository userRepository;
    private Long userId;

    public UserController(ContactProfileRepository contactProfileRepository, UserRepository userRepository) {
        this.contactProfileRepository = contactProfileRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/contacts")
    public Iterable<ContactProfile> contacts(Authentication authentication) {
        userId = Optional.of(authentication)
                .filter(JwtAuthenticationToken.class::isInstance)
                .map(JwtAuthenticationToken.class::cast)
                .map(jwt -> jwt.getTokenAttributes().get("id"))
                .map(Long.class::cast)
                .orElseThrow(RuntimeException::new);
        System.out.println(authentication);
        return contactProfileRepository.findAllByUserId(userId);
    }

    @PostMapping("/contact/save")
    public ResponseEntity<ContactProfile> save(@RequestBody ContactProfile contactProfile) {
        contactProfile.setUser(userRepository.findById(userId).orElseThrow());
        contactProfile.getPhoneNumbers().stream().filter(Objects::nonNull).forEach(phoneNumber -> phoneNumber.setContactProfile(contactProfile));
        contactProfile.getEmailAddresses().stream().filter(Objects::nonNull).forEach(emailAddress -> emailAddress.setContactProfile(contactProfile));
        return ResponseEntity.ok(contactProfileRepository.save(contactProfile));
    }

    @DeleteMapping("/contact/{id}")
    public void removeContact(@PathVariable Long id) {
        contactProfileRepository.deleteById(id);
    }
}

