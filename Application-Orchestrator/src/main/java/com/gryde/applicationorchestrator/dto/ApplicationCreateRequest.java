package com.gryde.applicationorchestrator.dto;

import com.gryde.contract.enums.*;

public record ApplicationCreateRequest(
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
        CardType cardTypeRequested
) {
}
