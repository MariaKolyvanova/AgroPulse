package ru.ochakovo.agropulse.dto.report;

public record FinancialXmlParsedRow(
        String indicatorCode,
        double reportingValue,
        Double previousPeriodValue,
        Double twoPeriodsAgoValue
) {
}
