package com.gryde.contract;

import java.math.BigDecimal;

public record ScoringRequest(
        Integer age,
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
        Boolean hasSalaryProject,
        Boolean hasDeposit,
        String cardTypeRequested,

        Integer totalAccounts,
        Integer activeAccounts,
        Integer closedAccounts,
        Integer defaultAccounts,
        Integer restructuredAccounts,

        Integer creditHistoryDays,
        BigDecimal totalCreditLimit,
        BigDecimal totalActiveDebt,
        BigDecimal utilizationRatio,

        Integer totalPayments,
        Integer dpd30,
        Integer dpd60,
        Integer dpd90,
        Integer dpd90Plus,
        Integer maxDaysOverdue,

        BigDecimal paymentRatio,
        Integer partialPaymentsCount,
        Integer recentOverdueCount,

        BigDecimal monthlyDebtPayment,
        BigDecimal debtToIncome
) {}
