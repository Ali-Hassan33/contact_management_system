package com.contact_management_system.exceptions;

import static java.lang.String.format;

public class EmailNotFoundException extends RuntimeException {
    public EmailNotFoundException(String email) {
        super(format("'%s' not found", email));
    }
}
