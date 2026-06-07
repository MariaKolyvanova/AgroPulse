package ru.ochakovo.agropulse.repository.esg;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.ochakovo.agropulse.entity.dictionary.RecommendationEsg;

import java.util.Collection;
import java.util.List;

public interface RecommendationEsgRepository extends JpaRepository<RecommendationEsg, Long> {

    @Query("""
            SELECT r FROM RecommendationEsg r
            JOIN FETCH r.indicatorsEsg
            WHERE r.indicatorsEsg.id IN :indicatorIds
            ORDER BY r.id
            """)
    List<RecommendationEsg> findByIndicatorIdsWithIndicator(Collection<Long> indicatorIds);
}
