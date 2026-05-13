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
@Table(name = "indicators")
public class Indicator extends BaseEntity {

    @Column(nullable = false)
    private String indicatorName;

    @Column(unique = true, nullable = false)
    private String code;

    /**
     * Родительский путь в XML: один тег или цепочка через «/» (например {@code Актив/ВнеОбА}).
     * Зарезервированные значения только для выбора корня раздела: {@code ОТЧЕТ_ФИН_РЕЗ}, {@code ОТЧЕТ_ДВИЖ_ДЕН_СР}.
     */
    @Column(name = "parent_tag")
    private String parentTag;

    private String xmlTag;

    @Enumerated(EnumType.STRING)
    @Column(name = "value_source", nullable = false)
    private ValueSource valueSource;

    private String formula;

    private String normativeValue;

    private String unit;
}
