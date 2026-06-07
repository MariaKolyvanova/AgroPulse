package ru.ochakovo.agropulse.repository.esg;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.ochakovo.agropulse.entity.data.ValuesIndicatorsEsg;

import java.util.List;

public interface ValuesIndicatorsEsgRepository extends JpaRepository<ValuesIndicatorsEsg, Long> {

    @Query("""
            SELECT v FROM ValuesIndicatorsEsg v
            JOIN FETCH v.indicatorsEsg
            WHERE v.company.id = :companyId AND v.reportYear = :reportYear
            """)
    List<ValuesIndicatorsEsg> findByCompanyIdAndReportYearWithIndicators(Long companyId, Short reportYear);

    void deleteAllByCompany_IdAndReportYear(Long companyId, Short reportYear);

    boolean existsByCompany_IdAndReportYear(Long companyId, Short reportYear);

    @Query("""
            SELECT DISTINCT v.reportYear
            FROM ValuesIndicatorsEsg v
            WHERE v.company.id = :companyId
            ORDER BY v.reportYear DESC
            """)
    List<Short> findDistinctYearsByCompanyId(Long companyId);
}
