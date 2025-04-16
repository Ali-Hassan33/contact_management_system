package com.contact_management_system.controllers;


import com.contact_management_system.dtos.ResetPasswordDto;
import com.contact_management_system.services.MimeService;
import jakarta.mail.MessagingException;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/mime")
public class MimeController {

    private final MimeService mimeService;

    public MimeController(MimeService mimeService) {
        this.mimeService = mimeService;
    }

    @PostMapping("/password/reset/{recipientEmail}")
    public void resetPasswordMail(@PathVariable String recipientEmail) throws MessagingException, IOException {
        mimeService.sendEmail(recipientEmail);
    }

    @PostMapping("/password/reset")
    public void resetPassword(@RequestBody ResetPasswordDto resetPasswordDto) {
        mimeService.resetPassword(resetPasswordDto);
    }
}
