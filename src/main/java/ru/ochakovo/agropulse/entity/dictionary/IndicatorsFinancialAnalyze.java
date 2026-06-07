package ru.ochakovo.agropulse.entity.dictionary;

import jakarta.persistence.*;
import lombok.*;
import ru.ochakovo.agropulse.entity.BaseEntity;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "indicators_financial_analyze")
public class IndicatorsFinancialAnalyze extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String code;

    @Column(columnDefinition = "text")
    private String formula;

    @Column(name = "normal_min", precision = 5, scale = 2)
    private BigDecimal normalMin;

    @Column(name = "normal_max", precision = 5, scale = 2)
    private BigDecimal normalMax;

    @Convert(converter = FinancialRatioCategoryConverter.class)
    @Column(length = 100)
    private FinancialRatioCategory category;

    @Column(length = 32)
    private String unit;
}
