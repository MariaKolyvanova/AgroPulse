package ru.ochakovo.agropulse.dto.audit;

import java.time.Instant;

public record UserAuditReadDto(
        Long companyId,
        String modifiedById,
        String modifiedByFullName,
        Long modifiedUserId,
        String modifiedUserFullName,
        Instant modifiedAt
){
        }
