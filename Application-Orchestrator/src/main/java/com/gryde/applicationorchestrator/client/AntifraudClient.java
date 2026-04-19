package com.gryde.applicationorchestrator.client;

import com.gryde.contract.AntifraudRequest;
import com.gryde.contract.AntifraudResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class AntifraudClient {

    private final RestClient antifraudRestClient;

    public AntifraudClient(@Qualifier("antifraudRestClient") RestClient antifraudRestClient) {
        this.antifraudRestClient = antifraudRestClient;
    }

    public AntifraudResponse antifraudCheck(AntifraudRequest request) {
        return antifraudRestClient.post()
                .uri("/internal/antifraud")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(AntifraudResponse.class);
    }
}
