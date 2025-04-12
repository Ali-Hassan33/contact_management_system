package com.contact_management_system.controllers;

import com.contact_management_system.entities.ContactProfile;
import com.contact_management_system.services.CSVService;
import com.contact_management_system.services.UserService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api")
public class UserController {

    private final UserService userService;

    private final CSVService csvService;

    public UserController(UserService userService, CSVService csvService) {
        this.userService = userService;
        this.csvService = csvService;
    }

    @GetMapping("/contacts")
    public Page<ContactProfile> contacts(Authentication authentication, @RequestParam int page, @RequestParam int pageSize) {
        return userService.fetchContacts(authentication, page, pageSize);
    }

    @GetMapping("/contacts/all")
    public Page<ContactProfile> contacts(Authentication authentication) {
        return userService.fetchContacts(authentication, null, null);
    }

    @PostMapping("/contact/save")
    public ResponseEntity<ContactProfile> save(@RequestBody ContactProfile contactProfile) {
        return ResponseEntity.ok(userService.saveContact(contactProfile));
    }

    @PutMapping(value = "/contact/update/{id}")
    public ResponseEntity<ContactProfile> update(@RequestBody ContactProfile contact, @PathVariable Long id) {
        return ResponseEntity.ok(userService.updateContact(contact, id));
    }

    @DeleteMapping("/contact/{id}")
    public void remove(@PathVariable Long id) {
        userService.deleteContact(id);
    }

    @PostMapping(value = "/contacts/import", consumes = "multipart/form-data")
    public void importContacts(@RequestParam("csvFile") MultipartFile file) {
        csvService.importCsv(file);
    }
}

