package com.contact_management_system.controllers;


import com.contact_management_system.services.MimeService;
import jakarta.mail.MessagingException;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/email")
public class MimeController {

    private final MimeService mimeService;

    public MimeController(MimeService mimeService) {
        this.mimeService = mimeService;
    }

    @PostMapping("/password/reset")
    public void resetPassword(@RequestBody String recipientEmail) throws MessagingException, IOException {
        mimeService.sendEmail(recipientEmail, "Password Reset");
    }
}
