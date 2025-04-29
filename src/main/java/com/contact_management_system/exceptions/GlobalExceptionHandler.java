package com.contact_management_system.exceptions;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EmailNotFoundException.class)
    ResponseEntity<String> handleEmailNotFoundException(HttpServletRequest request, EmailNotFoundException ex) {
        log.error("Request: {} raised {}: {}", request.getRequestURI(), ex.getClass().getSimpleName(), ex.getMessage());
        return new ResponseEntity<>("Email not found!", NOT_FOUND);
    }

    @ExceptionHandler(TokenExpiredException.class)
    public ResponseEntity<String> handleTokenExpiredException(TokenExpiredException ex) {
        log.error("TokenExpiredException: {}", ex.getMessage());
        return new ResponseEntity<>(ex.getMessage(), NOT_FOUND);
    }
}
