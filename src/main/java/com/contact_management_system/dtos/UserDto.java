package com.contact_management_system.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Value;

import java.io.Serializable;

/**
 * DTO for {@link com.contact_management_system.entities.User}
 */
@Value
@Builder
public class UserDto implements Serializable {
    Long id;

    @NotBlank
    String name;

    String password;

    @Email
    String email;
}