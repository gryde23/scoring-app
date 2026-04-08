package com.gryde.applicationorchestrator.dto;

import com.gryde.contract.enums.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ApplicationCreateRequest(

        @NotBlank
        String fullName,

        @NotNull
        Integer age,

        @NotNull
        Character gender,

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
        Boolean hasProperty,

        @NotNull
        Boolean hasCar,

        Integer existingCards,

        Integer existingLoans,

        Integer totalMonthlyDebt,

        @NotNull
        Boolean hasSalaryProject,

        @NotNull
        Boolean hasDeposit,

        @NotNull
        CardType cardTypeRequested,

        @NotNull
        UUID userUUID
) {
}
