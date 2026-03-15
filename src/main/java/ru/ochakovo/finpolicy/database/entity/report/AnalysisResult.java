package ru.ochakovo.finpolicy.database.entity.report;

import jakarta.persistence.*;
import lombok.*;
import ru.ochakovo.finpolicy.database.entity.BaseEntity;
import ru.ochakovo.finpolicy.database.entity.user.User;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "analysis_results")
public class AnalysisResult extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "report_id", unique = true)
    private FinancialReport report;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "analyst_id")
    private User analyst;

    @Column(name = "total_score")
    private Double totalScore;

    @Column(columnDefinition = "TEXT")
    private String comment;
}