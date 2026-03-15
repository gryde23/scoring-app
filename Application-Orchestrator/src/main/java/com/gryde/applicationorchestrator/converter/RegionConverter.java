package com.gryde.applicationorchestrator.converter;

import com.gryde.applicationorchestrator.enums.Region;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class RegionConverter implements AttributeConverter<Region, String> {
    @Override
    public String convertToDatabaseColumn(Region attribute) {
        return attribute == null ? null : attribute.getDbValue();
    }

    @Override
    public Region convertToEntityAttribute(String dbData) {
        return dbData == null ? null : Region.fromDbValue(dbData);
    }
}
