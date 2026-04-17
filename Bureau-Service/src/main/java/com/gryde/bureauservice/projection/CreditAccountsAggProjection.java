package com.gryde.bureauservice.projection;


import java.math.BigDecimal;

public interface CreditAccountsAggProjection {
    Integer getTotalAccounts();
    Integer getActiveAccounts();
    Integer getClosedAccounts();
    Integer getDefaultAccounts();
    Integer getRestructuredAccounts();
    Integer getCreditHistoryDays();
    BigDecimal getTotalCreditLimit();
    BigDecimal getTotalActiveDebt();
    BigDecimal getMonthlyDebtPayment();
    BigDecimal getUtilizationRatio();
}