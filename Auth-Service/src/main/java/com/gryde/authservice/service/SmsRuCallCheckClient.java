package com.gryde.authservice.service;

import com.gryde.authservice.dto.SmsRuCallCheckAddResponse;
import com.gryde.authservice.dto.SmsRuCallCheckStatusResponse;
import com.gryde.authservice.exception.SmsRuCallCheckException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
public class SmsRuCallCheckClient {

    @Value("${sms.sms-ru.api-id}")
    private String apiId;

    private final RestClient restClient = RestClient.builder()
            .baseUrl("https://sms.ru")
            .build();

    public SmsRuCallCheckAddResponse startVerification(String phone) {
        String phoneForProvider = phone.replace("+", "");

        SmsRuCallCheckAddResponse response = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/callcheck/add")
                        .queryParam("api_id", apiId)
                        .queryParam("phone", phoneForProvider)
                        .queryParam("json", "1")
                        .build())
                .retrieve()
                .body(SmsRuCallCheckAddResponse.class);

        if (response == null || !"OK".equals(response.status())) {
            throw new SmsRuCallCheckException("Не удалось создать проверку звонком");
        }

        return response;
    }

    public SmsRuCallCheckStatusResponse getStatus(String checkId) {
        SmsRuCallCheckStatusResponse response = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/callcheck/status")
                        .queryParam("api_id", apiId)
                        .queryParam("check_id", checkId)
                        .queryParam("json", "1")
                        .build())
                .retrieve()
                .body(SmsRuCallCheckStatusResponse.class);

        if (response == null || !"OK".equals(response.status())) {
            throw new SmsRuCallCheckException("Не удалось проверить статус звонка");
        }

        return response;
    }
}
