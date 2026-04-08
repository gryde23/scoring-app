package com.gryde.scoringservice;

import com.gryde.contract.ScoringRequest;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class MlScoringClient {

    private final RestTemplate mlRestTemplate;

    public MlScoringClient(@Qualifier("mlRestTemplate") RestTemplate mlRestTemplate) {
        this.mlRestTemplate = mlRestTemplate;
    }

    public MlScoringResponse predict(ScoringRequest request) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<ScoringRequest> entity = new HttpEntity<>(request, headers);

        return mlRestTemplate.postForObject(
                "/internal/ml/predict",
                entity,
                MlScoringResponse.class
        );
    }
}
