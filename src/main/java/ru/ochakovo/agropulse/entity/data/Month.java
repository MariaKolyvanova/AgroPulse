package ru.ochakovo.agropulse.entity.data;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.Getter;

public enum Month {
        JANUARY(1, "Январь"),
        FEBRUARY(2, "Февраль"),
        MARCH(3, "Март"),
        APRIL(4, "Апрель"),
        MAY(5, "Май"),
        JUNE(6, "Июнь"),
        JULY(7, "Июль"),
        AUGUST(8, "Август"),
        SEPTEMBER(9, "Сентябрь"),
        OCTOBER(10, "Октябрь"),
        NOVEMBER(11, "Ноябрь"),
        DECEMBER(12, "Декабрь");


    @Getter
    private final int number;

    @Getter
    private final String name;

    Month(int number, String name) {
        this.number = number;
        this.name = name;
    }

    public static Month fromNumber(int number) {
        for (Month month : values()) {
            if (month.number == number) {
                return month;
            }
        }
        throw new IllegalArgumentException("Unknown month number: " + number);
    }

    @Converter(autoApply = true)
    public static class JpaConverter implements AttributeConverter<Month, Short> {

        @Override
        public Short convertToDatabaseColumn(Month month) {
            return month == null ? null : (short) month.getNumber();
        }

        @Override
        public Month convertToEntityAttribute(Short dbData) {
            return dbData == null ? null : fromNumber(dbData);
        }
    }

}
