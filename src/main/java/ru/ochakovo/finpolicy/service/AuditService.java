package ru.ochakovo.finpolicy.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.ochakovo.finpolicy.entity.organization.AuditAction;
import ru.ochakovo.finpolicy.entity.organization.AuditLog;
import ru.ochakovo.finpolicy.entity.organization.EntityType;
import ru.ochakovo.finpolicy.entity.organization.User;
import ru.ochakovo.finpolicy.repository.AuditLogRepository;

@Service
@RequiredArgsConstructor
public class AuditService {

    private final AuditLogRepository auditLogRepository;

    public void log(User user, EntityType entityType, Long entityId, AuditAction action, String description) {
        AuditLog log = AuditLog.builder().user(user).entityType(entityType).entityId(entityId).action(action).description(description).build();

        auditLogRepository.save(log);
    }

    public String getLog(AuditLog log) {

        return String.format("id=%s, user=%s, action=%s, entityType=%s, entityId=%s, time=%s",
                log.getId(),
                log.getUser(),
                log.getAction(), log.getEntityType(), log.getEntityId(), log.getCreatedAt());
    }
}