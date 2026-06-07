package ru.ochakovo.agropulse.repository.xml;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.ochakovo.agropulse.entity.data.ValuesIndicatorsFinancialReports;

import java.util.List;

public interface ValuesIndicatorsFinancialReportsRepository extends JpaRepository<ValuesIndicatorsFinancialReports, Long> {

    List<ValuesIndicatorsFinancialReports> findByReportId(Long reportId);

    @Query("""
            SELECT v FROM ValuesIndicatorsFinancialReports v
            JOIN FETCH v.indicatorsFinancialReports
            WHERE v.report.id = :reportId
            """)
    List<ValuesIndicatorsFinancialReports> findByReportIdWithIndicators(Long reportId);

    void deleteAllByReport_Id(Long reportId);

    @Query("""
            select v.indicatorsFinancialReports.indicatorName
            from ValuesIndicatorsFinancialReports v
            where v.report.id = :id
            """)
    List<String> findIndicatorNamesByReportId(Long id);


    @Query("""
            SELECT v.value
            FROM ValuesIndicatorsFinancialReports v
            JOIN IndicatorsFinancialReports i
            ON v.indicatorsFinancialReports.id = i.id
            WHERE v.report.id = :reportId
            AND
            i.code = :code
            """)
    Double getValueByCode(Long reportId, String code);

    @Query("""
            SELECT v.previousPeriodValue
            FROM ValuesIndicatorsFinancialReports v
            JOIN IndicatorsFinancialReports i
            ON v.indicatorsFinancialReports.id = i.id
            WHERE v.report.id = :reportId
            AND i.code = :code
            """)
    Double getPreviousPeriodValueByCode(Long reportId, String code);

    @Query("""
            SELECT v.twoPeriodsAgoValue
            FROM ValuesIndicatorsFinancialReports v
            JOIN IndicatorsFinancialReports i
            ON v.indicatorsFinancialReports.id = i.id
            WHERE v.report.id = :reportId
            AND i.code = :code
            """)
    Double getTwoPeriodsAgoValueByCode(Long reportId, String code);

    @Query("""
        SELECT v FROM ValuesIndicatorsFinancialReports v
        JOIN FETCH v.indicatorsFinancialReports
        WHERE v.report.id = :reportId
        AND v.report.company.id = :companyId
        """)
    List<ValuesIndicatorsFinancialReports> findAllByReportIdAndCompanyId(Long reportId, Long companyId);

}
