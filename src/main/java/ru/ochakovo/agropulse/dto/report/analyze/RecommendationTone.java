package ru.ochakovo.agropulse.dto.report.analyze;

public enum RecommendationTone {
    POSITIVE,
    CRITICAL,
    WARNING,
    NEUTRAL;

    public static RecommendationTone fromPositive(Boolean positive) {
        if (positive == null) {
            return NEUTRAL;
        }
        return positive ? POSITIVE : WARNING;
    }

    public static RecommendationTone fromCondition(String condition) {
        if (condition == null || condition.isBlank()) {
            return NEUTRAL;
        }
        return switch (condition.toLowerCase()) {
            case "normal" -> POSITIVE;
            case "warning" -> WARNING;
            case "critical" -> CRITICAL;
            default -> NEUTRAL;
        };
    }
}
