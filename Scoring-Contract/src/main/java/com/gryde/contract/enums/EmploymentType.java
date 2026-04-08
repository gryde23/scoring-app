package com.gryde.contract.enums;

public enum EmploymentType {
    UNEMPLOYED("безработный"),
    BUSINESS("бизнес"),
    PENSIONER("пенсионер"),
    EMPLOYEE("наемный работник"),
    SELF_EMPLOYED("самозанятый");

    private final String dbValue;

    EmploymentType(String dbValue) {
        this.dbValue = dbValue;
    }

    public static EmploymentType fromDbValue(String dbValue) {
        for (EmploymentType type: values()) {
            if (type.dbValue.equals(dbValue)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown employment type: " + dbValue);
    }

    public String getDbValue() {
        return dbValue;
    }
}
