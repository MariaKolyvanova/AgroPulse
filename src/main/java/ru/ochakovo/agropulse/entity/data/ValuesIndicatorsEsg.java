package ru.ochakovo.agropulse.entity.data;

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
import ru.ochakovo.agropulse.entity.dictionary.IndicatorsEsg;
import ru.ochakovo.agropulse.entity.organization.Company;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "values_indicators_esg")
public class ValuesIndicatorsEsg extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @Column(name = "report_year", nullable = false)
    private Short reportYear;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "indicator_id")
    private IndicatorsEsg indicatorsEsg;

    @Column(nullable = false)
    private Double value;
}
