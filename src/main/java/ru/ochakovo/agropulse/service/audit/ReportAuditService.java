package ru.ochakovo.agropulse.service.audit;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import ru.ochakovo.agropulse.dto.audit.ReportAuditFilter;
import ru.ochakovo.agropulse.dto.audit.ReportAuditReadDto;
import ru.ochakovo.agropulse.entity.audit.ReportAudit;
import ru.ochakovo.agropulse.entity.data.Report;
import ru.ochakovo.agropulse.entity.data.ReportStatus;
import ru.ochakovo.agropulse.entity.organization.User;
import ru.ochakovo.agropulse.mapper.audit.ReportAuditReadMapper;
import ru.ochakovo.agropulse.querydsl.QPredicates;
import ru.ochakovo.agropulse.repository.audit.ReportAuditRepository;
import ru.ochakovo.agropulse.security.CustomUserDetails;
import ru.ochakovo.agropulse.util.AuditSorting;

import java.time.LocalDateTime;
import java.util.List;

import static ru.ochakovo.agropulse.entity.audit.QReportAudit.reportAudit;

@RequiredArgsConstructor
@Service
@Transactional
public class ReportAuditService {

    private final ReportAuditRepository reportAuditRepository;
    private final ReportAuditReadMapper reportAuditReadMapper;

    public Page<ReportAuditReadDto> findAll(ReportAuditFilter filter, Pageable pageable) {
        Long companyId = CustomUserDetails.requireCurrent().getCompanyId();

        var predicate = QPredicates.builder()
                .add(companyId, reportAudit.company.id::eq)
                .add(filter.fullName(), fullName ->
                        reportAudit.user.lastName
                                .concat(" ")
                                .concat(reportAudit.user.firstName)
                                .concat(" ")
                                .concat(reportAudit.user.patronymic.coalesce(""))
                                .containsIgnoreCase(fullName)

                )
                .add(StringUtils.hasText(filter.status()) ? filter.status().trim() : null, status ->
                        reportAudit.status.in(ReportStatus.matchingByLabel(status)))
                .add(filter.reportName(), reportAudit.report.reportName::containsIgnoreCase)
                .add(filter.from(), from ->
                        reportAudit.date.goe(from.atStartOfDay()))
                .add(filter.to(), to ->
                        reportAudit.date.lt(to.plusDays(1).atStartOfDay()))
                .build();

        return reportAuditRepository.findAll(predicate, AuditSorting.byDateDesc(pageable, "date"))
                .map(reportAuditReadMapper::map);
    }

    public List<ReportAuditReadDto> findAllById(Long id) {
        Long companyId = CustomUserDetails.requireCurrent().getCompanyId();

        return reportAuditRepository.findAllByReportIdAndCompanyIdOrderByDateDesc(id, companyId).stream()
                .map(reportAuditReadMapper::map).toList();
    }

    public void recordStatusChange(Report report, User user, ReportStatus status) {
        reportAuditRepository.save(
                ReportAudit.builder()
                        .company(report.getCompany())
                        .user(user)
                        .report(report)
                        .status(status)
                        .date(LocalDateTime.now())
                        .build()
        );
    }
}
