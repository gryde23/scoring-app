package com.gryde.applicationorchestrator.client;

import com.gryde.contract.scoring.ScoringRequest;
import com.gryde.contract.scoring.ScoringResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class ScoringClient {

    private final RestClient scoringRestClient;

    public ScoringClient(@Qualifier("scoringRestClient") RestClient scoringRestClient) {
        this.scoringRestClient = scoringRestClient;
    }

    public ScoringResponse calculate(ScoringRequest request) {
        return scoringRestClient.post()
                .uri("/internal/scoring/calculate")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(ScoringResponse.class);
    }
}
