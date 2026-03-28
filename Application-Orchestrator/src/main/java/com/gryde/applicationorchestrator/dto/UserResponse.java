package com.gryde.applicationorchestrator.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserResponse(
        UUID id,

        @Pattern(regexp = "^(\\+|)(7|8)( |)\\d{3}( |)\\d{3}( |)(\\d{2}( |)){2}$")
        String phone,

        @Email
        String email,

        LocalDateTime createdAt
) {
}
