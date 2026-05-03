package com.gryde.applicationorchestrator.admin.dto;

import com.gryde.contract.enums.*;

public record AdminUpdateApplicationRequest(
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
        String comment
) {
}
