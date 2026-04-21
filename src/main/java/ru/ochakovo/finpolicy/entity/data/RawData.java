package ru.ochakovo.finpolicy.entity.data;

import jakarta.persistence.*;
import lombok.*;
import ru.ochakovo.finpolicy.entity.BaseEntity;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "raw_data")
public class RawData extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "report_id")
    private Report report;

    private String indicatorName;

    private String value;

    private String errorText;
}