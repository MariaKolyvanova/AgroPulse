package ru.ochakovo.finpolicy.entity.data;

import jakarta.persistence.*;
import lombok.*;
import ru.ochakovo.finpolicy.entity.organization.Company;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "reports")
public class Report extends AbstractFile {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "companies_id")
    private Company company;

    private String period;
    private String documentType;

    @Enumerated(EnumType.STRING)
    private ReportStatus status;
}