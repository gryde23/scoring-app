package com.gryde.scoringservice;

import com.gryde.contract.ScoringRequest;
import com.gryde.contract.ScoringResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class ScoringService {

    private final InternalScoringEngine internalScoringEngine;
    private final MlScoringClient mlScoringClient;

    public ScoringResponse calculate(ScoringRequest request) {

        InternalScoringResponse internalScore = internalScoringEngine.calculateInternalScore(request);
        if (internalScore.internalScore() == -1) {
            return new ScoringResponse(0, BigDecimal.ONE, 0, internalScore.scoringReasons());
        }
        MlScoringResponse mlPrediction = mlScoringClient.predict(request);

        return new ScoringResponse(internalScore.internalScore(),
                mlPrediction.mlDefaultProbability(),
                mlPrediction.recommendedLimit(),
                internalScore.scoringReasons());
    }
}
