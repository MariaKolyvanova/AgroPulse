package ru.ochakovo.finpolicy.database.entity.analytics;

import jakarta.persistence.*;
import lombok.*;
import ru.ochakovo.finpolicy.database.entity.BaseEntity;
import ru.ochakovo.finpolicy.database.entity.organization.User;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "report_exports")
public class ReportExport extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "analysis_id")
    private AnalysisResult analysis;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "file_path", nullable = false)
    private String filePath;

    @Column(name = "export_format", nullable = false)
    private String exportFormat;

    @Column(name = "file_size")
    private Long fileSize;
}