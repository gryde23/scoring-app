package com.gryde.applicationorchestrator.dto;

import com.gryde.applicationorchestrator.enums.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.UUID;


public record ApplicationDTO(
        UUID id,

        @NotBlank
        String fullName,

        @NotNull
        Integer age,

        @NotNull
        char gender,

        @NotNull
        MaritalStatus maritalStatus,

        Integer dependents,

        @NotNull
        Education education,

        @NotNull
        Region region,

        @NotNull
        EmploymentType employmentType,

        Integer employmentLength,

        @NotNull
        Integer monthlyIncome,

        Integer additionalIncome,

        @NotNull
        boolean hasProperty,

        @NotNull
        boolean hasCar,

        Integer existingCards,

        Integer existingLoans,

        Integer totalMonthlyDebt,

        @NotNull
        boolean hasSalaryProject,

        @NotNull
        boolean hasDeposit,

        @NotNull
        CardType cardTypeRequested,

        @NotNull
        ApplicationStatus status,

        @NotNull
        LocalDateTime createdAt,

        @NotNull
        UUID userUUID,

        UUID applicationDecisionUUID
) {
}
