package ru.ochakovo.agropulse.dto.audit;

import ru.ochakovo.agropulse.entity.data.ReportStatus;

import java.time.LocalDate;

public record ReportAuditFilter (
        String fullName,
        ReportStatus status,
        String reportName,
        LocalDate from,
        LocalDate to
) {
}
