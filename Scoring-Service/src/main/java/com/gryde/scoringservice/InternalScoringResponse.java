package com.gryde.scoringservice;

import java.util.List;

public record InternalScoringResponse(
        int internalScore,
        List<String> scoringReasons
) {
}
