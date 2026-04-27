package com.gryde.authservice.dto;

public record SmsRuCallCheckAddResponse(
        String status,
        Integer status_code,
        String status_text,
        String check_id,
        String call_phone,
        String call_phone_pretty,
        String call_phone_html
) {
}
