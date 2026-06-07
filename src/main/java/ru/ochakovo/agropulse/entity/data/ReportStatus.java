package ru.ochakovo.agropulse.entity.data;

import java.util.Arrays;
import java.util.List;

public enum ReportStatus {
    UPLOADED("Загружен"),
    XML_REPLACED("Добавлен новый документ"),
    APPROVED_FOR_ANALYSIS("Утверждён к анализу"),
    CALCULATIONS_COMPLETED("Проведены расчёты"),
    COMMENTS_ADDED("Добавлены комментарии"),
    APPROVED("Одобрен");

    private final String label;

    ReportStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public static List<ReportStatus> matchingByLabel(String pattern) {
        if (pattern == null || pattern.isBlank()) {
            return List.of();
        }
        String lower = pattern.toLowerCase().trim();
        return Arrays.stream(values())
                .filter(status -> status.label.toLowerCase().contains(lower))
                .toList();
    }

}
