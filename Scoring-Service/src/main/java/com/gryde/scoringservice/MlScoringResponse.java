package com.gryde.scoringservice;

import java.math.BigDecimal;

public record MlScoringResponse(
        BigDecimal mlDefaultProbability,
        Integer recommendedLimit
) {}