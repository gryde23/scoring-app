package com.gryde.applicationorchestrator.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Bean
    public RestClient scoringRestClient(
            @Value("${services.scoring.url}") String scoringBaseUrl
    ) {
        return RestClient.builder()
                .baseUrl(scoringBaseUrl)
                .build();
    }

    @Bean
    public RestClient bureauRestClient(
            @Value("${services.bureau.url}") String bureauBaseUrl
    ) {
        return RestClient.builder()
                .baseUrl(bureauBaseUrl)
                .build();
    }
}
