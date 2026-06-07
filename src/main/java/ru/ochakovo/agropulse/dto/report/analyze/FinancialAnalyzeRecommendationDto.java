package ru.ochakovo.agropulse.dto.report.analyze;

public record FinancialAnalyzeRecommendationDto(
        String indicatorName,
        String type,
        String description
) {
}
