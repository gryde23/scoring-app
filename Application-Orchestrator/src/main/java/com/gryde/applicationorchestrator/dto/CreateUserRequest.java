package com.gryde.applicationorchestrator.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;

public record CreateUserRequest(
        @Pattern(regexp = "^7\\d{10}$")
        String phone,

        @Email
        String email
) {
}
