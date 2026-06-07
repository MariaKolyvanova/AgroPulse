package ru.ochakovo.agropulse.dto.report.data;

public record IndicatorsReportsReadDto(
        String name,
        Double twoPeriodsAgoValue,
        Double previousPeriodValue,
        Double currentValue
) {
}
