package com.gryde.contract;

import com.gryde.contract.enums.Decision;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record ApplicationDecisionDTO(
        UUID id,
        Integer bureauScore,
        Integer internalScore,
        BigDecimal mlDefaultProbability,
        Integer antifraudScore,
        List<String> antifraudFlags,
        Decision finalDecision,
        Integer approvedLimit,
        List<String> decisionReasons,
        LocalDateTime createdAt,
        UUID applicationUUID
) {
}
