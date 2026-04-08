package com.gryde.applicationorchestrator.converter;

import com.gryde.contract.enums.Education;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class EducationConverter implements AttributeConverter<Education, String> {
    @Override
    public String convertToDatabaseColumn(Education attribute) {
        return attribute == null ? null : attribute.getDbValue();
    }

    @Override
    public Education convertToEntityAttribute(String dbData) {
        return dbData == null ? null : Education.fromDbValue(dbData);
    }
}
