package com.gryde.authservice.dto;

import java.util.UUID;

public record AuthResponse(
        UUID userId,
        String accessToken,
        String tokenType,
        long expiresIn
) {
}
