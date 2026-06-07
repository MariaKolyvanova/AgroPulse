package ru.ochakovo.agropulse.dto.report.analyze;

import java.util.List;

public record FinancialAnalyzeCategoryTableDto(
        String categoryName,
        List<FinancialAnalyzeIndicatorRowDto> rows,
        List<FinancialAnalyzeRecommendationDto> recommendations
) {
}
