package ru.ochakovo.agropulse.entity.dictionary;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.ochakovo.agropulse.entity.BaseEntity;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "recommendations_esg")
public class RecommendationEsg extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "indicator_id", nullable = false)
    private IndicatorsEsg indicatorsEsg;

    @Column(name = "condition_rule", nullable = false)
    private String conditionRule;

    @Column(name = "conclusion_text")
    private String conclusionText;

    @Column(name = "recommendation_text")
    private String recommendationText;

    @Column(name = "is_positive", nullable = false)
    private Boolean positive;
}
