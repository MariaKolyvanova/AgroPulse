package ru.ochakovo.finpolicy.database.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;
import ru.ochakovo.finpolicy.database.entity.organization.AuditLog;
import ru.ochakovo.finpolicy.database.entity.organization.Company;

import java.util.List;

@Repository
public interface AudLogRepository extends
        JpaRepository<AuditLog, Long>,
        QuerydslPredicateExecutor<AuditLog> {

    List<AuditLog> findAllByUserId(Long userId);

    List<AuditLog> findAllByAction(String action);
}
