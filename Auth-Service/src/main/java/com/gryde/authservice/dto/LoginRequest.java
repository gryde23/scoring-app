package com.gryde.authservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record LoginRequest(

        @NotBlank
        @Pattern(regexp = "^(\\+7)\\d{10}$", message = "Неверный формат номера")
        String phone,

        @NotBlank(message = "Пароль не может быть пустым")
        String password
) {
}
