package ru.ochakovo.finpolicy.dto;

import ru.ochakovo.finpolicy.entity.organization.AuditAction;
import ru.ochakovo.finpolicy.entity.organization.EntityType;

import java.time.Instant;

public record AuditLogDto(
        Long id,
        Long userId,
        AuditAction action,
        EntityType entityType,
        Long entityId,
        Instant createdAt
) {
}