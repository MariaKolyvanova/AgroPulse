package ru.ochakovo.agropulse.dto.audit;

import java.time.LocalDate;

public record UserAuditFilter (
        String modifiedByFullName,
        String modifiedUserFullName,
        LocalDate from,
        LocalDate to
        ) {
}
