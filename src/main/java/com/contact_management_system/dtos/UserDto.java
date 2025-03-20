package com.contact_management_system.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Value;

import java.io.Serializable;

/**
 * DTO for {@link com.contact_management_system.entities.User}
 */
@Value
public class UserDto implements Serializable {
    Long id;

    @NotBlank
    String name;

    @NotBlank
    String password;

    @Email
    String email;
}