package com.gryde.applicationorchestrator.client;

import com.gryde.contract.BureauSnapshotResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.UUID;

@Component
public class BureauClient {

    private final RestClient bureauRestClient;

    public BureauClient(@Qualifier("bureauRestClient") RestClient scoringRestClient) {
        this.bureauRestClient = scoringRestClient;
    }

    public BureauSnapshotResponse calculate(UUID userId) {
        return bureauRestClient.get()
                .uri(uriBuilder -> uriBuilder.path("/internal/bureau")
                        .queryParam("userId", userId)
                        .build())
                .retrieve()
                .body(BureauSnapshotResponse.class);
    }
}
