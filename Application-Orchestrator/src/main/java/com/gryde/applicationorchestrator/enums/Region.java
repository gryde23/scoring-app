package com.gryde.applicationorchestrator.enums;

import lombok.Getter;

@Getter
public enum Region {
    MOSCOW("Москва"),
    SPB("Санкт-Петербург"),
    REGIONAL_CENTER("региональный центр"),
    OTHER("другой город");

    private final String dbValue;

    Region(String dbValue) {
        this.dbValue = dbValue;
    }

    public static Region fromDbValue(String dbValue) {
        for (Region region: values()) {
            if (region.dbValue.equals(dbValue)) {
                return region;
            }
        }
        throw new IllegalArgumentException("Unknown region: " + dbValue);
    }
}
