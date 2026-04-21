package ru.ochakovo.finpolicy.entity.data;

import jakarta.persistence.*;
import lombok.*;
import ru.ochakovo.finpolicy.entity.organization.Organization;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "reports")
public class Report extends AbstractFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id")
    private Organization organization;

    private String period;
    private String documentType;

    @Enumerated(EnumType.STRING)
    private ReportStatus status;
}