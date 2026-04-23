package ru.ochakovo.agropulse.service;

import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ochakovo.agropulse.dto.AuditLogDto;
import ru.ochakovo.agropulse.dto.AuditLogFilter;
import ru.ochakovo.agropulse.entity.organization.AuditAction;
import ru.ochakovo.agropulse.entity.organization.AuditLog;
import ru.ochakovo.agropulse.entity.organization.EntityType;
import ru.ochakovo.agropulse.entity.organization.User;
import ru.ochakovo.agropulse.mapper.AuditLogMapper;
import ru.ochakovo.agropulse.repository.AuditLogRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;
    private final CurrentUserService currentUserService;
    private final AuditLogMapper auditLogMapper;

    @Transactional
    public void log(
            EntityType entityType,
            String entityName,
            AuditAction action,
            String description
           ) {

        User currentUser = currentUserService.getCurrentUser();

        AuditLog log = AuditLog.builder()
                .company(currentUser.getCompany())
                .user(currentUser)
                .entityType(entityType)
                .entityName(entityName)
                .action(action)
                .description(description)
                .build();

        auditLogRepository.save(log);
    }

    public String getLog(AuditLog log) {

        return String.format("id=%s, user=%s, action=%s, entityType=%s, entityId=%s, time=%s",
                log.getId(),
                log.getUser(),
                log.getAction(), log.getEntityType(), log.getEntityName(), log.getCreatedAt());
    }

    @Transactional(readOnly = true)
    public Page<AuditLogDto> findAll(AuditLogFilter filter, Pageable pageable) {

        Specification<AuditLog> spec = buildSpec(filter);

        return auditLogRepository.findAll(spec, pageable)
                .map(auditLogMapper::toDto);
    }

    private Specification<AuditLog> buildSpec(AuditLogFilter filter) {
        return (root, query, cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            if (filter.userId() != null) {
                predicates.add(cb.equal(root.get("user").get("id"), filter.userId()));
            }

            if (filter.entityType() != null) {
                predicates.add(cb.equal(root.get("entityType"), filter.entityType()));
            }

            if (filter.entityName() != null) {
                predicates.add(cb.equal(root.get("entityName"), filter.entityName()));
            }

            if (filter.action() != null) {
                predicates.add(cb.equal(root.get("action"), filter.action()));
            }

            if (filter.from() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), filter.from()));
            }

            if (filter.to() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("createdAt"), filter.to()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

}