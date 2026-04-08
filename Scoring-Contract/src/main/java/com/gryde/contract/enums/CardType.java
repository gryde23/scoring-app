package com.gryde.contract.enums;

public enum CardType {
    GOLD("золотая"),
    PLATINUM("платиновая"),
    STANDARD("стандартная");

    private final String dbValue;

    CardType(String dbValue) {
        this.dbValue = dbValue;
    }

    public static CardType fromDbValue(String dbValue) {
        for (CardType cardType: values()) {
            if (cardType.dbValue.equals(dbValue)) {
                return cardType;
            }
        }
        throw new IllegalArgumentException("Unknown card type: " + dbValue);
    }

    public String getDbValue() {
        return dbValue;
    }
}
