package ru.ochakovo.agropulse.dto.report;

import ru.ochakovo.agropulse.entity.data.ReportStatus;

import java.time.Instant;

public record ReportReadDto(
        Long id,
        Long companyId,
        Short reportYear,
        Short reportMonth,
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
