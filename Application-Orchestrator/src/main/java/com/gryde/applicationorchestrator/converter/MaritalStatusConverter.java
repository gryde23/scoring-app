package com.gryde.applicationorchestrator.converter;

import com.gryde.applicationorchestrator.enums.MaritalStatus;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class MaritalStatusConverter implements AttributeConverter<MaritalStatus, String> {
    @Override
    public String convertToDatabaseColumn(MaritalStatus attribute) {
        return attribute == null ? null : attribute.getDbValue();
    }

    @Override
    public MaritalStatus convertToEntityAttribute(String dbData) {
        return dbData == null ? null : MaritalStatus.fromDbValue(dbData);
    }
}
