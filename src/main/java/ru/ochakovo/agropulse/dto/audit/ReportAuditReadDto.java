package ru.ochakovo.agropulse.dto.audit;

import ru.ochakovo.agropulse.entity.data.ReportStatus;

import java.time.Instant;

public record ReportAuditReadDto (
        Long id,
        Long companyId,
        Long userId,
        String fullName,
        ReportStatus status,
        Long reportId,
        String reportName,
        Instant date
) {
}
