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
@Table(name = "indicators_financial_reports")
public class IndicatorsFinancialReports extends BaseEntity {

    @Column(name = "indicator_name", nullable = false)
    private String indicatorName;

    @Column(unique = true, nullable = false)
    private String code;

    @Column(name = "parent_tag")
    private String parentTag;

    private String xmlTag;
}
