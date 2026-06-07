package ru.ochakovo.agropulse.repository.analyze;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.ochakovo.agropulse.entity.dictionary.IndicatorsFinancialAnalyze;

import java.util.Optional;

public interface IndicatorsFinancialAnalyzeRepository extends JpaRepository<IndicatorsFinancialAnalyze, Long> {

    Optional<IndicatorsFinancialAnalyze> findByCode(String code);
}
