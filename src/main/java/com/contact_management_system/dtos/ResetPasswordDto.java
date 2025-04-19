package com.contact_management_system.dtos;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ResetPasswordDto {

    String token;

    String newPassword;
}
