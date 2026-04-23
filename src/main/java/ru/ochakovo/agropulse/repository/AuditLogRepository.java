package ru.ochakovo.agropulse.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;
import ru.ochakovo.agropulse.entity.organization.AuditLog;

import java.util.List;

@Repository
public interface AuditLogRepository extends
        JpaRepository<AuditLog, Long>,
        QuerydslPredicateExecutor<AuditLog>,
        JpaSpecificationExecutor<AuditLog> {

    List<AuditLog> findAllByUserId(Long userId);

    List<AuditLog> findAllByAction(String action);

}
