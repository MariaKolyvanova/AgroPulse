package ru.ochakovo.agropulse.entity.dictionary;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor
public enum EsgCategory {
    ENVIRONMENTAL("Экология"),
    SOCIAL("Социальные показатели"),
    GOVERNANCE("Корпоративное управление");

    private final String label;

    private static final Map<String, EsgCategory> BY_LABEL = Arrays.stream(values())
            .collect(Collectors.toMap(EsgCategory::getLabel, Function.identity()));

    public static EsgCategory fromDatabaseValue(String value) {
        if (value == null) {
            return null;
        }
        try {
            return valueOf(value);
        } catch (IllegalArgumentException ignored) {
            EsgCategory category = BY_LABEL.get(value);
            if (category == null) {
                throw new IllegalArgumentException("Неизвестная категория ESG: " + value);
            }
            return category;
        }
    }
}
