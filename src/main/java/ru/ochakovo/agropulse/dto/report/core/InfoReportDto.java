package ru.ochakovo.agropulse.dto.report.core;

import java.util.List;

public record InfoReportDto(
        int size,
        List<String> indicators
) {
}
