package ru.ochakovo.finpolicy.database.entity.analytics;

import jakarta.persistence.*;
import lombok.*;
import ru.ochakovo.finpolicy.database.entity.BaseEntity;
import ru.ochakovo.finpolicy.database.entity.FileMetadata;
import ru.ochakovo.finpolicy.database.entity.organization.User;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "report_exports")
public class ReportExport extends FileMetadata {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "analysis_results_id")
    private AnalysisResult analysis;

    @Column(name = "export_format", nullable = false)
    private String exportFormat;
}