package ru.ochakovo.agropulse.entity.dictionary;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor
public enum FinancialRatioCategory {
    GENERAL_CHARACTERISTICS("Общая характеристика"),
    LIQUIDITY("Ликвидность"),
    LIQUIDITY_COEFFICIENTS("Коэффициенты ликвидности"),
    FINANCIAL_STABILITY("Финансовая устойчивость"),
    SOLVENCY("Платежеспособность"),
    PROFITABILITY("Рентабельность");

    private final String label;

    private static final Map<String, FinancialRatioCategory> BY_LABEL = Arrays.stream(values())
            .collect(Collectors.toMap(FinancialRatioCategory::getLabel, Function.identity()));

    public static FinancialRatioCategory fromDatabaseValue(String value) {
        if (value == null) {
            return null;
        }
        try {
            return valueOf(value);
        } catch (IllegalArgumentException ignored) {
            FinancialRatioCategory category = BY_LABEL.get(value);
            if (category == null) {
                throw new IllegalArgumentException("Неизвестная категория показателя: " + value);
            }
            return category;
        }
    }
}
