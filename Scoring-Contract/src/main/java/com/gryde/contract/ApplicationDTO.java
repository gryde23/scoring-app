package com.gryde.contract;

import com.gryde.contract.enums.*;

import java.time.LocalDateTime;
import java.util.UUID;


public record ApplicationDTO(
        UUID id,
        String fullName,
        Integer age,
        char gender,
        MaritalStatus maritalStatus,
        Integer dependents,
        Education education,
        Region region,
        EmploymentType employmentType,
        Integer employmentLength,
        Integer monthlyIncome,
        Integer additionalIncome,
        boolean hasProperty,
        boolean hasCar,
        Integer existingCards,
        Integer existingLoans,
        Integer totalMonthlyDebt,
        boolean hasSalaryProject,
        boolean hasDeposit,
        CardType cardTypeRequested,
        ApplicationStatus status,
        LocalDateTime createdAt,
        UUID userUUID,
        UUID applicationDecisionUUID
) {
}
