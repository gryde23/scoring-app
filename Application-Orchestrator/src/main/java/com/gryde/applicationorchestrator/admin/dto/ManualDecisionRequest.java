package com.gryde.applicationorchestrator.admin.dto;

public record ManualDecisionRequest (
        ManualDecision decision,
        Integer approvedLimit,
        String reason,
        String comment
){
}
