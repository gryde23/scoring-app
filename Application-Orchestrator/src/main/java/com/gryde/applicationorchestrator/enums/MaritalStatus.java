package com.gryde.applicationorchestrator.enums;

import lombok.Getter;

@Getter
public enum MaritalStatus {
    WIDOWED("вдовец/вдова"),
    MARRIED("в браке"),
    DIVORCED("разведен/а"),
    SINGLE("одинок/а");

    private final String dbValue;

    MaritalStatus(String dbValue) {
        this.dbValue = dbValue;
    }

    public static MaritalStatus fromDbValue(String dbValue) {
        for (MaritalStatus status: values()) {
            if (status.dbValue.equals(dbValue)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown marital status: " + dbValue);
    }
}
