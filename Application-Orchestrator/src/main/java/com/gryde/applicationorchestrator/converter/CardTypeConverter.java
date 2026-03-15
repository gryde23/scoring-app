package com.gryde.applicationorchestrator.converter;

import com.gryde.applicationorchestrator.enums.CardType;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class CardTypeConverter implements AttributeConverter<CardType, String> {
    @Override
    public String convertToDatabaseColumn(CardType attribute) {
        return attribute == null ? null : attribute.getDbValue();
    }

    @Override
    public CardType convertToEntityAttribute(String dbData) {
        return dbData == null ? null : CardType.fromDbValue(dbData);
    }
}
