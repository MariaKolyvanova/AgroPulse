package ru.ochakovo.agropulse.dto.report.analyze;

public record FinancialAnalyzeSummaryRowDto(
        String sectionTitle,
        String indicatorName,
        String unit,
        int decimalPlaces,
        Double twoPeriodsAgoValue,
        Double previousPeriodValue,
        Double currentValue
) {
}
