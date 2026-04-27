package com.gryde.applicationorchestrator.dto;

import com.gryde.contract.enums.ApplicationStatus;
import com.gryde.contract.enums.FinalDecision;

import java.time.LocalDateTime;
import java.util.UUID;

public record ApplicationShortResponse(
        UUID id,
        LocalDateTime createdAt,
        ApplicationStatus status
) {
}
