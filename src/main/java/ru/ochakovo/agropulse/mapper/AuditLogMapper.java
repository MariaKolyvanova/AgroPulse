package ru.ochakovo.finpolicy.mapper;

import ru.ochakovo.finpolicy.dto.AuditLogDto;
import ru.ochakovo.finpolicy.entity.organization.AuditLog;

public class AuditLogMapper {

    public AuditLogDto toDto(AuditLog log) {
        return new AuditLogDto(
                log.getCompany(),
                log.getUser(),
                log.getEntityType(),
                log.getEntityName(),
                log.getAction(),
                log.getDescription(),
                log.getCreatedAt(),
                log.getUpdatedAt()
        );
    }
}