package com.gryde.applicationorchestrator.admin.dto;

import com.gryde.contract.enums.CardType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record ManualReviewApplicationResponse(
        UUID applicationId,
        String fullName,
        CardType cardTypeRequested,
        LocalDateTime createdAt
) {
}
