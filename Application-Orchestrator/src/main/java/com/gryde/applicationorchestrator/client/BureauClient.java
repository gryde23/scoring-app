package com.gryde.applicationorchestrator.client;

import com.gryde.contract.AddAccountToBureauRequest;
import com.gryde.contract.BureauResultResponse;
import com.gryde.contract.BureauSnapshotResponse;
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

    public BureauResultResponse calculate(UUID userId) {
        return bureauRestClient.get()
                .uri(uriBuilder -> uriBuilder.path("/internal/bureau")
                        .queryParam("userId", userId)
                        .build())
                .retrieve()
                .body(BureauResultResponse.class);
    }

    public void addAccountToBureau(AddAccountToBureauRequest request) {
        bureauRestClient.post()
                .uri("/internal/bureau")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request);
    }
}
