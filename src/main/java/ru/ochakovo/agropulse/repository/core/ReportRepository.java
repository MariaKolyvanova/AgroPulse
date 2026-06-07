package ru.ochakovo.agropulse.repository.core;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.ochakovo.agropulse.entity.data.Report;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReportRepository  extends JpaRepository<Report, Long>, QuerydslPredicateExecutor<Report> {

    Optional<Report> findByIdAndCompanyId(Long id, Long companyId);

    @Query("""
            SELECT DISTINCT r.reportName
            FROM Report r
            WHERE r.company.id = :companyId
            ORDER BY r.reportName
            """)
    List<String> findDistinctReportNamesByCompanyId(@Param("companyId") Long companyId);

}
