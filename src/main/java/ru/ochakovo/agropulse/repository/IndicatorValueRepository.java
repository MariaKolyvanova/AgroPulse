package ru.ochakovo.agropulse.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.ochakovo.agropulse.entity.data.IndicatorValue;

import java.util.List;

public interface IndicatorValueRepository extends JpaRepository<IndicatorValue, Long> {

    List<IndicatorValue> findByReport_Id(Long reportId);

    void deleteAllByReport_Id(Long reportId);

    @Query("""
        select iv.indicator.indicatorName
        from IndicatorValue iv
        where iv.report.id = :id
    """)
    List<String> findIndicatorNamesByReportId(Long id);
}
