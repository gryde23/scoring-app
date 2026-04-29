package com.gryde.authservice.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record VerificationRequest(
        @NotNull
        UUID verificationId
) {
}
