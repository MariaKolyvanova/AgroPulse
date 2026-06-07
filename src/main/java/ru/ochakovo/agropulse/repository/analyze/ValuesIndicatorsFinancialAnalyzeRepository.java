package ru.ochakovo.agropulse.repository.analyze;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.ochakovo.agropulse.entity.data.ValuesIndicatorsFinancialAnalyze;

import java.util.List;

public interface ValuesIndicatorsFinancialAnalyzeRepository extends JpaRepository<ValuesIndicatorsFinancialAnalyze, Long> {

    List<ValuesIndicatorsFinancialAnalyze> findByReport_Id(Long reportId);

    @Query("""
            SELECT v FROM ValuesIndicatorsFinancialAnalyze v
            JOIN FETCH v.indicatorsFinancialAnalyze
            WHERE v.report.id = :reportId
            """)
    List<ValuesIndicatorsFinancialAnalyze> findByReportIdWithIndicators(Long reportId);

    void deleteAllByReport_Id(Long reportId);
}
