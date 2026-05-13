package ru.ochakovo.agropulse.dto.report;

import ru.ochakovo.agropulse.entity.data.Month;
import ru.ochakovo.agropulse.entity.data.ReportStatus;

public record ReportFilter(
    Month month,
    Short year,
    String reportName,
    ReportStatus status
) {
}
