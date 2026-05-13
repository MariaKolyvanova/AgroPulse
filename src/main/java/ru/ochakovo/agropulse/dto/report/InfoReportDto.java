package ru.ochakovo.agropulse.dto.report;

import java.util.List;

public record InfoReportDto(
        int size,
        List<String> indicators
) {
}
