package ru.ochakovo.agropulse.repository.xml;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.ochakovo.agropulse.entity.dictionary.IndicatorsFinancialReports;

import java.util.List;
import java.util.Optional;

public interface IndicatorsFinancialReportsRepository extends JpaRepository<IndicatorsFinancialReports, Long> {

    Optional<IndicatorsFinancialReports> findByCode(String code);

    List<IndicatorsFinancialReports> findByXmlTag(String xmlTag);

    @Query("""
            select l from IndicatorsFinancialReports l
            where l.xmlTag is not null
              and trim(l.xmlTag) <> ''
              and trim(l.xmlTag) <> '-'
            """)
    List<IndicatorsFinancialReports> findAllForXmlImport();
}
