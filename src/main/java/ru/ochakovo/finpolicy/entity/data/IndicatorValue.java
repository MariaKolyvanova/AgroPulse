package ru.ochakovo.finpolicy.entity.data;

import jakarta.persistence.*;
import lombok.*;
import ru.ochakovo.finpolicy.entity.BaseEntity;
import ru.ochakovo.finpolicy.entity.dictionary.Indicator;

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
}