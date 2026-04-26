package com.gryde.authservice.dto;

public record StartRegistrationResponse(
        String verificationId,
        String message
) {
}
