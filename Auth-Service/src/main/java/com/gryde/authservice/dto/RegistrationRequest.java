package com.gryde.authservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RegistrationRequest(

        @NotBlank
        @NotNull
        String registrationToken,

        @NotBlank(message = "Пароль не может быть пустым")
        @Size(min = 8, max = 30, message = "Пароль должен содержать от 8 до 30 символов")
        String password
) {
}
