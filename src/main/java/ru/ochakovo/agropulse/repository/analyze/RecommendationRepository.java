package ru.ochakovo.agropulse.repository.analyze;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.ochakovo.agropulse.entity.dictionary.Recommendation;

import java.util.Collection;
import java.util.List;

public interface RecommendationRepository extends JpaRepository<Recommendation, Long> {

    @Query("""
            SELECT r FROM Recommendation r
            JOIN FETCH r.indicatorsFinancialAnalyze
            WHERE r.indicatorsFinancialAnalyze.id IN :indicatorIds
            ORDER BY r.id
            """)
    List<Recommendation> findByIndicatorIdsWithIndicator(Collection<Long> indicatorIds);
}
