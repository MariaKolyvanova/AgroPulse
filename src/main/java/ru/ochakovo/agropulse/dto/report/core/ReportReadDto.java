package ru.ochakovo.agropulse.dto.report.core;

import ru.ochakovo.agropulse.entity.data.Month;
import ru.ochakovo.agropulse.entity.data.ReportStatus;

import java.time.Instant;

public record ReportReadDto(
        Long id,
        Long companyId,
        Short reportYear,
        Month reportMonth,
        String reportName,
        ReportStatus status,
        String originalFilename,
        String storedFilename,
        String contentType,
        Long fileSize,
        Instant createdAt,
        Instant updatedAt
) {
}
