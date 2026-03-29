package com.gryde.surveyservice;

import java.util.List;

public record InternalScoringResponse(
        int internalScore,
        List<String> scoringReasons
) {
}
