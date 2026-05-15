package ru.ochakovo.agropulse.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;
import ru.ochakovo.agropulse.entity.audit.AuthAudit;

@Repository
public interface AuthAuditRepository
        extends JpaRepository<AuthAudit, Long>, QuerydslPredicateExecutor<AuthAudit> {

}