package ru.ochakovo.agropulse.entity.data;

import jakarta.persistence.*;
import lombok.*;
import ru.ochakovo.agropulse.entity.BaseEntity;
import ru.ochakovo.agropulse.entity.dictionary.IndicatorsFinancialAnalyze;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "values_indicators_financial_analyze")
public class ValuesIndicatorsFinancialAnalyze extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "report_id")
    private Report report;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "financial_indicator_id")
    private IndicatorsFinancialAnalyze indicatorsFinancialAnalyze;

    @Column(nullable = false)
    private Double value;

    @Column(name = "previous_period_value")
    private Double previousPeriodValue;

    @Column(name = "two_periods_ago_value")
    private Double twoPeriodsAgoValue;
}
