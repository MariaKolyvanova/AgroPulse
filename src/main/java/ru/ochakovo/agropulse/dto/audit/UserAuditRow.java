package ru.ochakovo.agropulse.dto.audit;

import java.time.Instant;

public interface UserAuditRow {

    Long getCompanyId();

    Long getModifiedById();

    String getModifiedByFullName();

    Long getModifiedUserId();

    String getModifiedUserFullName();

    Instant getModifiedAt();
}
