package com.gryde.bureauservice.dto;

import com.gryde.bureauservice.enums.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record PaymentHistoryDto(
        UUID id,
        UUID accountId,
        LocalDate dueDate,
        BigDecimal amountDue,
        BigDecimal amountPaid,
        Integer daysOverdue,
        PaymentStatus status
) {
}
