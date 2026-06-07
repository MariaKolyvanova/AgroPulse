package ru.ochakovo.agropulse.dto.report.analyze;

public record FinancialAnalyzeIndicatorBlockDto(
        String indicatorCode,
        String indicatorName,
        String unit,
        ChartSeriesType seriesType,
        int decimalPlaces,
        Double twoPeriodsAgoValue,
        Double previousPeriodValue,
        Double currentValue,
        String deltaLabel,
        String conclusion,
        String recommendation,
        RecommendationTone tone,
        String seriesColor,
        String normalValue
) {
}
