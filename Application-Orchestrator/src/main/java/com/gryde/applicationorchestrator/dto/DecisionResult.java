package com.gryde.applicationorchestrator.dto;

import com.gryde.contract.enums.FinalDecision;

import java.util.List;

public record DecisionResult(
        FinalDecision finalDecision,
        Integer approvedLimit,
        List<String> decisionReasons
) {
}
