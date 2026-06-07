package ru.ochakovo.agropulse.entity.dictionary;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class FinancialRatioCategoryConverter implements AttributeConverter<FinancialRatioCategory, String> {

    @Override
    public String convertToDatabaseColumn(FinancialRatioCategory category) {
        return category == null ? null : category.getLabel();
    }

    @Override
    public FinancialRatioCategory convertToEntityAttribute(String dbValue) {
        return FinancialRatioCategory.fromDatabaseValue(dbValue);
    }
}
