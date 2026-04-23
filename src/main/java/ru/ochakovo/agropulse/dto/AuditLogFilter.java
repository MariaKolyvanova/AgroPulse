package ru.ochakovo.agropulse.dto;

import ru.ochakovo.agropulse.entity.organization.AuditAction;
import ru.ochakovo.agropulse.entity.organization.EntityType;

import java.time.Instant;

public record AuditLogFilter(
        Long userId,
        EntityType entityType,
        String entityName,
        AuditAction action,
        Instant from,
        Instant to
) {}