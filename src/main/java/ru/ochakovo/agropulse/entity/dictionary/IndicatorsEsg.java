package ru.ochakovo.agropulse.entity.dictionary;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
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
@Table(name = "indicators_esg")
public class IndicatorsEsg extends BaseEntity {

    @Column(unique = true, nullable = false)
    private String code;

    @Column(name = "indicator_name", nullable = false)
    private String indicatorName;

    @Convert(converter = EsgCategoryConverter.class)
    @Column(nullable = false, length = 100)
    private EsgCategory category;
}
