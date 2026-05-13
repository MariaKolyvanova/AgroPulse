package ru.ochakovo.agropulse.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.transaction.annotation.Transactional;
import ru.ochakovo.agropulse.dto.audit.ReportAuditFilter;
import ru.ochakovo.agropulse.dto.audit.ReportAuditReadDto;
import ru.ochakovo.agropulse.entity.audit.ReportAudit;
import ru.ochakovo.agropulse.entity.data.Report;
import ru.ochakovo.agropulse.entity.data.ReportStatus;
import ru.ochakovo.agropulse.entity.organization.User;
import ru.ochakovo.agropulse.mapper.audit.ReportAuditReadMapper;
import ru.ochakovo.agropulse.querydsl.QPredicates;
import ru.ochakovo.agropulse.repository.ReportAuditRepository;
import ru.ochakovo.agropulse.repository.ReportRepository;
import ru.ochakovo.agropulse.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static ru.ochakovo.agropulse.entity.audit.QReportAudit.reportAudit;

@RequiredArgsConstructor
@Service
@Transactional
public class ReportAuditService {

    private final ReportAuditRepository reportAuditRepository;
    private final ReportAuditReadMapper reportAuditReadMapper;
    private final ReportRepository reportRepository;
    private final UserRepository userRepository;

    public Page<ReportAuditReadDto> findAll(ReportAuditFilter filter, Pageable pageable) {

        var predicate = QPredicates.builder()
                .add(filter.fullName(), fullName ->
                        reportAudit.user.lastName
                                .concat(" ")
                                .concat(reportAudit.user.firstName)
                                .concat(" ")
                                .concat(reportAudit.user.patronymic.coalesce(""))
                                .containsIgnoreCase(fullName)

                )
                .add(filter.status(), reportAudit.status::eq)
                .add(filter.reportName(), reportAudit.report.reportName::containsIgnoreCase)
                .add(filter.from(), from ->
                        reportAudit.date.goe(from.atStartOfDay()))
                .add(filter.to(), to ->
                        reportAudit.date.lt(to.plusDays(1).atStartOfDay()))
                .build();

        return reportAuditRepository.findAll(predicate, pageable).map(reportAuditReadMapper::map);
    }

    public List<ReportAuditReadDto> findAllById(Long id) {
        return reportAuditRepository.getAllByReportId(id).stream()
                .map(reportAuditReadMapper::map).toList();
    }

    /**
     * История по отчёту только если отчёт принадлежит указанной компании (без утечки по id отчёта).
     */
    public List<ReportAuditReadDto> findAllByReportForCompany(Long reportId, Long companyId) {
        reportRepository.findByIdAndCompany_Id(reportId, companyId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        return findAllById(reportId);
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

    /**
     * То же по идентификаторам — удобно из контроллеров, где есть только id отчёта и пользователя.
     */
    public void recordStatusChange(Long reportId, Long userId, ReportStatus status) {
        Report report = reportRepository.getReferenceById(reportId);
        User user = userId != null ? userRepository.getReferenceById(userId) : null;
        recordStatusChange(report, user, status);
    }
}
