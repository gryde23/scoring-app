package com.gryde.applicationorchestrator.client;

import com.gryde.contract.scoring.ScoringRequest;
import com.gryde.contract.scoring.ScoringResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.UUID;

@Component
public class BureauClient {

    private final RestClient bureauRestClient;

    public BureauClient(@Qualifier("bureauRestClient") RestClient scoringRestClient) {
        this.bureauRestClient = scoringRestClient;
    }

    public Integer calculate(UUID userId) {
        return bureauRestClient.get()
                .uri(uriBuilder -> uriBuilder.path("/internal/bureau")
                        .queryParam("userId", userId)
                        .build())
                .retrieve()
                .body(Integer.class);
    }

    public String setUserUUID(UUID userId, String phone) {
        return bureauRestClient.post()
                .uri(uriBuilder -> uriBuilder.path("/internal/bureau")
                        .queryParam("userId", userId)
                        .queryParam("phone", phone)
                        .build())
                .retrieve()
                .body(String.class);
    }
}
