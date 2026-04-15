package com.gryde.applicationorchestrator.service;

import com.gryde.contract.AntifraudResponse;
import com.gryde.contract.ScoringResponse;
import com.gryde.contract.enums.FinalDecision;
import org.springframework.stereotype.Component;

@Component
public class DecisionEngine {
    public FinalDecision decide(ScoringResponse scoring, Integer bureauScore, AntifraudResponse fraud) {
        if (fraud.antifraudScore() < 500) return FinalDecision.REJECTED;
        if (bureauScore < 300)            return FinalDecision.REJECTED;

        int total = scoring.internalScore() + bureauScore / 10;
        if (total < 400) return FinalDecision.REJECTED;
        if (total < 700) return FinalDecision.MANUAL_REVIEW;
        return FinalDecision.APPROVED;
    }
}
