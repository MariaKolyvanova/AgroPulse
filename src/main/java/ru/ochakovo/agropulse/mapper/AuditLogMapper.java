package ru.ochakovo.agropulse.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.ochakovo.agropulse.dto.AuditLogDto;
import ru.ochakovo.agropulse.entity.organization.AuditLog;

@Mapper(componentModel = "spring")
public interface AuditLogMapper {

    @Mapping(target = "fullName", source = "user.fullName")
    AuditLogDto toDto(AuditLog auditLog);
}