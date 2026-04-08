package ru.ochakovo.finpolicy.database.entity.analytics;

import jakarta.persistence.*;
import lombok.*;
import ru.ochakovo.finpolicy.database.entity.BaseEntity;
import ru.ochakovo.finpolicy.database.entity.dictionary.Recommendation;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "accepted_recommendations")
public class AcceptedRecommendation extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "analysis_id", nullable = false)
    private AnalysisResult analysis;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recommendation_id")
    private Recommendation recommendation;

    @Column(columnDefinition = "TEXT")
    private String description; // Текст рекомендации, скопированный из справочника на момент анализа

    @Column(nullable = false)
    private String status; // ACCEPTED / REJECTED (можно сделать Enum, если нужно)
}