package com.gryde.authservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.util.UUID;

public record VerificationRequest(
        @NotNull
        UUID verificationId,

        @NotBlank
        @Pattern(regexp = "\\d{6}")
        String code
) {
}
