package ru.ochakovo.agropulse.entity.data;

import jakarta.persistence.*;
import lombok.*;
import ru.ochakovo.agropulse.entity.BaseEntity;
import ru.ochakovo.agropulse.entity.dictionary.IndicatorsFinancialReports;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "values_indicators_financial_reports")
public class ValuesIndicatorsFinancialReports extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "report_id")
    private Report report;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "indicator_id")
    private IndicatorsFinancialReports indicatorsFinancialReports;

    @Column(nullable = false)
    private Double value;

    @Column(name = "previous_period_value")
    private Double previousPeriodValue;

    @Column(name = "two_periods_ago_value")
    private Double twoPeriodsAgoValue;
}
