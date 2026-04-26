package com.gryde.authservice.dto;

import jakarta.validation.constraints.Pattern;

public record StartRegistrationRequest(
        @Pattern(regexp = "^(\\+7)\\d{10}$", message = "Неверный формат номера")
        String phone
) {
}
