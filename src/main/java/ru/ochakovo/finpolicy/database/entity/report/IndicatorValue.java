package ru.ochakovo.finpolicy.database.entity.report;

import jakarta.persistence.*;
import lombok.*;
import ru.ochakovo.finpolicy.database.entity.BaseEntity;
import ru.ochakovo.finpolicy.database.entity.dictionary.Indicator;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "indicator_values")
public class IndicatorValue extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "report_id")
    private FinancialReport report;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "indicator_id")
    private Indicator indicator;

    @Column(nullable = false)
    private Double value;
}