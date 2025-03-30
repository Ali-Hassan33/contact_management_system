package com.contact_management_system.exceptions;

public class EmailNotFoundException extends RuntimeException {
    public EmailNotFoundException() {
        super("email not found.");
    }
}
