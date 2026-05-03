package com.gryde.contract;

import com.gryde.contract.enums.FinalDecision;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record DecisionResponse(
        UUID id,
        Integer bureauScore,
        Integer internalScore,
        BigDecimal mlDefaultProbability,
        Integer antifraudScore,
        List<String> antifraudFlags,
        FinalDecision finalDecision,
        Integer approvedLimit,
        List<String> decisionReasons,
        LocalDateTime createdAt
) {
}
