package com.gryde.applicationorchestrator.dto;

import com.gryde.contract.ApplicationResponse;
import com.gryde.contract.enums.*;

import java.util.List;

public record ApplicationWithDecisionResponse(
        ApplicationResponse applicationResponse,
        DecisionResult decisionResult
) {
}
