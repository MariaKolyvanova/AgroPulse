package ru.ochakovo.agropulse.mapper;

import org.mapstruct.Mapper;
import ru.ochakovo.agropulse.dto.audit.UserAuditReadDto;
import ru.ochakovo.agropulse.dto.audit.UserAuditRow;

@Mapper(componentModel = "spring")
public interface UserAuditReadMapper {

    UserAuditReadDto map(UserAuditRow row);
}
