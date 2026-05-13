package ru.ochakovo.agropulse.dto.audit;

import java.time.LocalDate;

public record AuthAuditFilter(
        String fullName,
        String ip,
        LocalDate from,
        LocalDate to
) {
}
