package com.gryde.bureauservice.dto;

import com.gryde.bureauservice.enums.AccountStatus;
import com.gryde.bureauservice.enums.AccountType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record CreditAccountDto(
        UUID id,
        UUID userId,
        String phone,
        AccountType accountType,
        LocalDate openDate,
        LocalDate closeDate,
        BigDecimal originalAmount,
        BigDecimal currentBalance,
        AccountStatus status,
        String bankName
) {
}
