package ru.ochakovo.agropulse.dto.audit;

import java.time.Instant;

public record AuthAuditReadDto(
        Long id,
        Long companyId,
        String fullName,
        Long userId,
        Instant authAt,
        String ip
) {
}
