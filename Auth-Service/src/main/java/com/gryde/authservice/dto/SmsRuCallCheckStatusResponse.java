package com.gryde.authservice.dto;

public record SmsRuCallCheckStatusResponse(
        String status,
        Integer status_code,
        String status_text,
        String check_status,
        String check_status_text
) {
}
