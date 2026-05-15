package ru.ochakovo.agropulse.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;
import ru.ochakovo.agropulse.entity.audit.ReportAudit;

import java.util.List;

@Repository
public interface ReportAuditRepository extends JpaRepository<ReportAudit, Long>, QuerydslPredicateExecutor<ReportAudit> {

    List<ReportAudit> findAllByReportIdAndCompanyId(Long id, Long companyId);
}
