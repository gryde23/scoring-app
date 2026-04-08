package com.gryde.contract;

import java.util.UUID;

public record ScoringRequest(
        Integer age,
        Character gender,
        String maritalStatus,
        Integer dependents,
        String education,
        String region,
        String employmentType,
        Integer employmentLength,
        Integer monthlyIncome,
        Integer additionalIncome,
        Boolean hasProperty,
        Boolean hasCar,
        Integer existingCards,
        Integer existingLoans,
        Integer totalMonthlyDebt,
        Boolean hasSalaryProject,
        Boolean hasDeposit,
        String cardTypeRequested
) {}
