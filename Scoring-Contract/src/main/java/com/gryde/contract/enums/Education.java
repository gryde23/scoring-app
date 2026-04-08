package com.gryde.contract.enums;

public enum Education {
    HIGHER("высшее"),
    SECONDARY("среднее"),
    SPECIAL("среднее специальное"),
    DEGREE("ученая степень");

    private final String dbValue;

    Education(String dbValue) {
        this.dbValue = dbValue;
    }

    public static Education fromDbValue(String dbValue) {
        for (Education education: values()) {
            if (education.dbValue.equals(dbValue)) {
                return education;
            }
        }
        throw new IllegalArgumentException("Unknown education type: " + dbValue);
    }

    public String getDbValue() {
        return dbValue;
    }
}
