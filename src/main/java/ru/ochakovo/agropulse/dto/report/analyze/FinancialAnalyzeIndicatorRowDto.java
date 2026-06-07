package ru.ochakovo.agropulse.dto.report.analyze;

public record FinancialAnalyzeIndicatorRowDto(
        String indicatorName,
        Double twoPeriodsAgoValue,
        Double previousPeriodValue,
        Double currentValue,
        String normalValue,
        Double normalMin,
        Double normalMax
) {
}
