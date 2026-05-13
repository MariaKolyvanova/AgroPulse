package ru.ochakovo.agropulse.entity.data;

import jakarta.persistence.*;
import lombok.*;
import ru.ochakovo.agropulse.entity.BaseEntity;
import ru.ochakovo.agropulse.entity.dictionary.Indicator;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "indicator_values")
public class IndicatorValue extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "report_id")
    private Report report;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "indicator_id")
    private Indicator indicator;

    @Column(nullable = false)
    private Double value;

    /** Предыдущий период (баланс: СумПрдщ; ОФР: СумПред). */
    private Double previousPeriodValue;

    /** Позапрошлый период (баланс: СумПрдшв). */
    private Double twoPeriodsAgoValue;
}