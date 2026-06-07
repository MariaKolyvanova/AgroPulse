package ru.ochakovo.agropulse.entity.dictionary;

import jakarta.persistence.*;
import lombok.*;
import ru.ochakovo.agropulse.entity.BaseEntity;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "recommendations")
public class Recommendation extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "indicator_id", nullable = false)
    private IndicatorsFinancialAnalyze indicatorsFinancialAnalyze;

    @Column(name = "condition_rule", nullable = false)
    private String conditionRule;

    @Column(name = "conclusion_text")
    private String conclusionText;

    @Column(name = "recommendation_text")
    private String recommendationText;

    @Column(nullable = false, length = 20)
    private String condition;
}
