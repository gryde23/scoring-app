package com.gryde.authservice.dto;

import java.time.LocalDateTime;

public record StartRegistrationResponse(
        String verificationId,
        String callPhone,
        String callPhonePretty,
        LocalDateTime expiresAt,
        String message
) {
}
