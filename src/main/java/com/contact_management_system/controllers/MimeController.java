package com.contact_management_system.controllers;


import com.contact_management_system.dtos.ResetPasswordDto;
import com.contact_management_system.services.MimeService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/mime")
public class MimeController {

    private final MimeService mimeService;

    public MimeController(MimeService mimeService) {
        this.mimeService = mimeService;
    }

    @PostMapping("/password/reset/{recipientEmail}")
    void resetPasswordMail(@PathVariable String recipientEmail) {
        mimeService.sendEmail(recipientEmail);
    }

    @PostMapping("/password/reset")
    void resetPassword(@RequestBody ResetPasswordDto resetPasswordDto) {
        mimeService.resetPassword(resetPasswordDto);
    }
}
