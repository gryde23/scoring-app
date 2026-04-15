package com.gryde.bureauservice.projection;

import java.math.BigDecimal;

public interface PaymentHistoryAggProjection {
    Integer getTotalPayments();
    Integer getDpd30();
    Integer getDpd60();
    Integer getDpd90Plus();
    Integer getMaxDaysOverdue();
    BigDecimal getPaymentRatio();
    Integer getPartialPaymentsCount();
    Integer getRecentOverdueCount();
}
