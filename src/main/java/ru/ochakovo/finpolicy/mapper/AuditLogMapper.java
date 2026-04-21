package ru.ochakovo.finpolicy.mapper;

import ru.ochakovo.finpolicy.dto.AuditLogDto;
import ru.ochakovo.finpolicy.entity.organization.AuditLog;

public class AuditLogMapper {

    public AuditLogDto toDto(AuditLog log) {
        return new AuditLogDto(
                log.getId(),
                log.getUser().getId(),
                log.getAction(),
                log.getEntityType(),
                log.getEntityId(),
                log.getCreatedAt()
        );
    }
}