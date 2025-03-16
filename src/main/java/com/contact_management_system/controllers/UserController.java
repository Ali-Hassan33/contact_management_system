package com.contact_management_system.controllers;

import com.contact_management_system.entities.ContactProfile;
import com.contact_management_system.repositories.ContactProfileRepository;
import com.contact_management_system.repositories.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:3000")
public class UserController {

    private final ContactProfileRepository contactProfileRepository;
    private final UserRepository userRepository;

    public UserController(ContactProfileRepository contactProfileRepository,
                          UserRepository userRepository) {
        this.contactProfileRepository = contactProfileRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/greet")
    @PreAuthorize("hasRole('USER')")
    public String publicPage(Authentication authentication) {
        return "Hello, " + authentication.getName();
    }

    @GetMapping("/contacts")
    public Iterable<ContactProfile> contacts() {
        return contactProfileRepository.findAllByUserId(10L); // todo
    }

    @PostMapping("/contact/save")
    public ResponseEntity<ContactProfile> save(@RequestHeader Long userId, @RequestBody ContactProfile contactProfile) {
        contactProfile.setUser(userRepository.findById(userId).orElseThrow());
        contactProfile.getPhoneNumbers().forEach(phoneNumber -> phoneNumber.setContactProfile(contactProfile));
        contactProfile.getEmailAddresses().forEach(emailAddress -> emailAddress.setContactProfile(contactProfile));
        contactProfileRepository.save(contactProfile);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/contact/{id}")
    public void removeContact(@PathVariable Long id) {
        contactProfileRepository.deleteById(id);
    }
}

