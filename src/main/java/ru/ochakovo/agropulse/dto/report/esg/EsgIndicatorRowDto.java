package ru.ochakovo.agropulse.dto.report.esg;

public record EsgIndicatorRowDto(
        Long indicatorId,
        String indicatorName,
        Double value
) {
}
