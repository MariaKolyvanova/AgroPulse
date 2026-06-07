package ru.ochakovo.agropulse.dto.report.analyze;

public record IndicatorRecommendationPairDto(
        String conclusion,
        String recommendation,
        RecommendationTone tone
) {
}
