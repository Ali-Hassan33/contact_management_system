package com.contact_management_system.exceptions;

public class JwtException extends RuntimeException {
    public JwtException(Exception e) {
        super("failed to generate jwt", e);
    }
}
