package ru.ochakovo.agropulse.dto.report.core;

import java.util.List;

public record WarningDto(
    List<String> missingAnalyzeIndicators,
    List<String> missingReportIndicators
)
{
}
