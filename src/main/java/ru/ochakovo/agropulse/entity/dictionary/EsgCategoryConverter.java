package ru.ochakovo.agropulse.entity.dictionary;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class EsgCategoryConverter implements AttributeConverter<EsgCategory, String> {

    @Override
    public String convertToDatabaseColumn(EsgCategory category) {
        return category == null ? null : category.getLabel();
    }

    @Override
    public EsgCategory convertToEntityAttribute(String dbValue) {
        return EsgCategory.fromDatabaseValue(dbValue);
    }
}
