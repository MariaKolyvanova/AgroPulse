package ru.ochakovo.agropulse.dto.audit;

import java.time.LocalDate;

public record ReportAuditFilter (
        String fullName,
        String status,
        String reportName,
        LocalDate from,
        LocalDate to
) {
}
