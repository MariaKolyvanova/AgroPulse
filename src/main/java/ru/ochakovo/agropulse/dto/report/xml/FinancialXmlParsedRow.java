package ru.ochakovo.agropulse.dto.report.xml;

public record FinancialXmlParsedRow(
        String indicatorCode,
        double reportingValue,
        Double previousPeriodValue,
        Double twoPeriodsAgoValue
) {
}
