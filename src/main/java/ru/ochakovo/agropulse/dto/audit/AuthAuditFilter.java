package ru.ochakovo.agropulse.dto;

import java.time.Instant;

public record AuthAuditFilter(
        String fullName,
        String ip,
        Instant from,
        Instant to
) {
}
