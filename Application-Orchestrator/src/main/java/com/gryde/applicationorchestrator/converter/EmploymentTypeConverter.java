package com.gryde.applicationorchestrator.converter;

import com.gryde.applicationorchestrator.enums.EmploymentType;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class EmploymentTypeConverter implements AttributeConverter<EmploymentType, String> {
    @Override
    public String convertToDatabaseColumn(EmploymentType attribute) {
        return attribute == null ? null : attribute.getDbValue();
    }

    @Override
    public EmploymentType convertToEntityAttribute(String dbData) {
        return dbData == null ? null : EmploymentType.fromDbValue(dbData);
    }
}
