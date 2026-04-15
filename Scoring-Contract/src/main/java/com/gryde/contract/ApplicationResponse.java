package com.gryde.contract;

import com.gryde.contract.enums.*;

import java.time.LocalDateTime;
import java.util.UUID;


public record ApplicationResponse(
        UUID id,
        UUID userId,
        String fullName,
        Integer age,
        MaritalStatus maritalStatus,
        Integer dependents,
        Education education,
        Region region,
        EmploymentType employmentType,
        Integer employmentLength,
        Integer monthlyIncome,
        Integer additionalIncome,
        Boolean hasProperty,
        Boolean hasCar,
        Boolean hasSalaryProject,
        Boolean hasDeposit,
        CardType cardTypeRequested,
        ApplicationStatus status,
        LocalDateTime createdAt
) {
}
