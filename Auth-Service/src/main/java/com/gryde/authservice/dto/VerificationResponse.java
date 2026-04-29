package com.gryde.authservice.dto;

import com.gryde.authservice.dto.enums.VerificationStatus;

public record VerificationResponse(
        VerificationStatus status,
        String registrationToken
) {
}
