package ru.ochakovo.finpolicy.entity.dictionary;

import jakarta.persistence.*;
import lombok.*;
import ru.ochakovo.finpolicy.entity.BaseEntity;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "recommendations")
public class Recommendation extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "indicator_id")
    private Indicator indicator;

    private String name;

    private String description;

    private String conditionRule;
}