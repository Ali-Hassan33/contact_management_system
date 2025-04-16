package com.contact_management_system.dtos;

import lombok.Builder;
import lombok.Value;

import java.util.UUID;

@Value
@Builder
public class ResetPasswordDto {

    String token;

    String newPassword;
}
