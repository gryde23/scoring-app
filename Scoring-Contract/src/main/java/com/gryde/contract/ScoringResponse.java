package com.gryde.contract;

import java.math.BigDecimal;
import java.util.List;

public record ScoringResponse(
        Integer internalScore,
        BigDecimal mlDefaultProbability,
        Integer recommendedLimit,
        List<String> scoringReasons
) {}
