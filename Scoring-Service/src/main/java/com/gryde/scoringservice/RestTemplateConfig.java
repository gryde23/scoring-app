package com.gryde.scoringservice;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

@Configuration
public class RestTemplateConfig {

    @Bean("mlRestTemplate")
    public RestTemplate mlRestTemplate(
            @Value("${services.ml.url}") String mlBaseUrl
    ) {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setUriTemplateHandler(new DefaultUriBuilderFactory(mlBaseUrl));
        return restTemplate;
    }
}