package com.gryde.applicationorchestrator.dto;

import com.gryde.contract.AntifraudResponse;
import com.gryde.contract.ScoringResponse;

public record ServiceResponses(
        ScoringResponse scoringResponse,
        Integer bureauScore,
        AntifraudResponse antifraudResponse
) {
}
