package ru.ochakovo.agropulse.dto;

import ru.ochakovo.agropulse.entity.organization.AuditAction;
import ru.ochakovo.agropulse.entity.organization.EntityType;

import java.time.Instant;

public record AuditLogDto(
        Long id,
        String fullName,
        EntityType entityType,
        String entityName,
        AuditAction action,
        String description,
        Instant createdAt
) {
}