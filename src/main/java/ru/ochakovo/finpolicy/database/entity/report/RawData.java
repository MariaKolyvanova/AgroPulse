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
@Table(name = "raw_data")
public class RawData extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "report_id")
    private FinancialReport report;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "indicator_id")
    private Indicator indicator;

    @Column(name = "row_index")
    private Integer rowIndex;

    @Column(name = "cell_address")
    private String cellAddress;

    @Column(name = "raw_value", columnDefinition = "TEXT")
    private String rawValue;

    @Builder.Default
    @Column(name = "is_valid")
    private Boolean isValid = true;
}