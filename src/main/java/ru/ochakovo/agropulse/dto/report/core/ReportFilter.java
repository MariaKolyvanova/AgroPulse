package ru.ochakovo.agropulse.dto.report.core;

import ru.ochakovo.agropulse.entity.data.Month;

public record ReportFilter(
    Month month,
    Short year,
    String reportName,
    String status
) {
}
