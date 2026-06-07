package ru.ochakovo.agropulse.service.report.core;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import ru.ochakovo.agropulse.dto.report.core.InfoReportDto;
import ru.ochakovo.agropulse.dto.report.core.ReportCreateDto;
import ru.ochakovo.agropulse.dto.report.core.ReportFilter;
import ru.ochakovo.agropulse.dto.report.core.ReportReadDto;
import ru.ochakovo.agropulse.entity.data.Comment;
import ru.ochakovo.agropulse.entity.data.Report;
import ru.ochakovo.agropulse.entity.data.ReportStatus;
import ru.ochakovo.agropulse.entity.organization.User;
import ru.ochakovo.agropulse.mapper.report.core.ReportCreateMapper;
import ru.ochakovo.agropulse.mapper.report.core.ReportReadMapper;
import ru.ochakovo.agropulse.querydsl.QPredicates;
import ru.ochakovo.agropulse.repository.core.CommentRepository;
import ru.ochakovo.agropulse.repository.core.ReportRepository;
import ru.ochakovo.agropulse.repository.organization.CompanyRepository;
import ru.ochakovo.agropulse.repository.organization.UserRepository;
import ru.ochakovo.agropulse.repository.xml.ValuesIndicatorsFinancialReportsRepository;
import ru.ochakovo.agropulse.security.CustomUserDetails;
import ru.ochakovo.agropulse.service.audit.ReportAuditService;
import ru.ochakovo.agropulse.service.report.analyze.CalculatorService;
import ru.ochakovo.agropulse.service.report.xml.FinancialReportIndicatorImportService;
import ru.ochakovo.agropulse.util.AuditSorting;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static ru.ochakovo.agropulse.entity.data.QReport.report;

@RequiredArgsConstructor
@Service
public class ReportService {

    private final ReportRepository reportRepository;
    private final ReportReadMapper reportReadMapper;
    private final ReportCreateMapper reportCreateMapper;
    private final CompanyRepository companyRepository;
    private final FileStorageService fileStorageService;
    private final FinancialReportIndicatorImportService importService;
    private final CalculatorService calculatorService;
    private final ReportAuditService reportAuditService;
    private final UserRepository userRepository;
    private final ValuesIndicatorsFinancialReportsRepository valuesIndicatorsFinancialReportsRepository;
    private final CommentRepository commentRepository;

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(".xml");

    public ReportReadDto findById(Long id) {
        Long companyId = CustomUserDetails.requireCurrent().getCompanyId();

        return reportRepository.findByIdAndCompanyId(id, companyId)
                .map(reportReadMapper::map).orElseThrow();
    }

    public void requirePrepare(Long id) {
        ReportWorkflow.requirePrepare(requireReportEntity(id).getStatus());
    }

    public void requireOpenAnalysis(Long id) {
        ReportWorkflow.requireOpenAnalysis(requireReportEntity(id).getStatus());
    }

    public void requireExportPdf(Long id) {
        ReportWorkflow.requireExportPdf(requireReportEntity(id).getStatus());
    }

    private Report requireReportEntity(Long id) {
        Long companyId = CustomUserDetails.requireCurrent().getCompanyId();
        return reportRepository.findByIdAndCompanyId(id, companyId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @Transactional
    public ReportReadDto create(MultipartFile multipartFile,
                                ReportCreateDto dto) throws XMLStreamException, IOException {

        validate(multipartFile);

        String originalFilename = StringUtils.cleanPath(multipartFile.getOriginalFilename());

        String extension = resolveExtension(originalFilename);
        String storedFilename = UUID.randomUUID() + extension;

        User user = userRepository.findById(CustomUserDetails.requireCurrent().getUserId()).orElseThrow();

        try {
            fileStorageService.save(multipartFile, storedFilename);

            Report report = reportCreateMapper.map(dto);
            report.setCompany(user.getCompany());
            report.setStatus(ReportStatus.UPLOADED);
            report.setOriginalFilename(originalFilename);
            report.setStoredFilename(storedFilename);
            report.setContentType(
                    StringUtils.hasText(multipartFile.getContentType())
                            ? multipartFile.getContentType()
                            : "application/octet-stream"
                                 );
            report.setFileSize(multipartFile.getSize());

            reportRepository.save(report);

            try (InputStream xmlStream = fileStorageService.openStream(storedFilename)) {
                importService.importFromXml(report, xmlStream);
            }

            reportAuditService.recordStatusChange(report, user, ReportStatus.UPLOADED);

            return reportReadMapper.map(report);

        } catch (Exception e) {
            fileStorageService.delete(storedFilename);
            throw e;
        }
    }

    @Transactional
    public void replaceXmlFile(Long id, MultipartFile file) throws XMLStreamException, IOException {
        validate(file);

        Report report = requireReportEntity(id);
        ReportWorkflow.requireReplaceXml(report.getStatus());

        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
        String newStoredFilename = UUID.randomUUID() + resolveExtension(originalFilename);
        String oldStoredFilename = report.getStoredFilename();

        try {
            fileStorageService.save(file, newStoredFilename);

            try (InputStream xmlStream = fileStorageService.openStream(newStoredFilename)) {
                importService.importFromXml(report, xmlStream);
            }

            report.setOriginalFilename(originalFilename);
            report.setStoredFilename(newStoredFilename);
            report.setContentType(
                    StringUtils.hasText(file.getContentType())
                            ? file.getContentType()
                            : "application/octet-stream"
            );
            report.setFileSize(file.getSize());

            ReportStatus newStatus = ReportStatus.XML_REPLACED;
            report.setStatus(newStatus);
            reportRepository.save(report);

            User user = userRepository.findById(CustomUserDetails.requireCurrent().getUserId()).orElseThrow();
            reportAuditService.recordStatusChange(report, user, newStatus);

            fileStorageService.delete(oldStoredFilename);
        } catch (Exception e) {
            fileStorageService.delete(newStoredFilename);
            throw e;
        }
    }

    private void validate(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Файл пустой");
        }

        String ext = resolveExtension(file.getOriginalFilename());

        if (!ALLOWED_EXTENSIONS.contains(ext)) {
            throw new IllegalArgumentException("Загрузите файл в формате XML");
        }
    }

    private String resolveExtension(String filename) {
        if (filename == null) return "";
        int i = filename.lastIndexOf('.');
        return (i > 0) ? filename.substring(i).toLowerCase() : "";
    }

    @Transactional(readOnly = true)
    public List<String> findReportNames(Long companyId) {
        return reportRepository.findDistinctReportNamesByCompanyId(companyId);
    }

    public Page<ReportReadDto> findAll(ReportFilter filter, Pageable pageable) {
        Long companyId = CustomUserDetails.requireCurrent().getCompanyId();

        var predicate = QPredicates.builder()
                .add(companyId, report.company.id::eq)
                .add(filter.year(), report.reportYear::eq)
                .add(filter.month(), report.reportMonth::eq)
                .add(filter.reportName(), report.reportName::containsIgnoreCase)
                .add(StringUtils.hasText(filter.status()) ? filter.status().trim() : null, status ->
                        report.status.in(ReportStatus.matchingByLabel(status)))
                .build();

        return reportRepository.findAll(predicate, AuditSorting.byDateDesc(pageable, "createdAt"))
                .map(reportReadMapper::map);
    }

    @Transactional(readOnly = true)
    public InfoReportDto getInfo(Long id) {

        Long companyId = CustomUserDetails.requireCurrent().getCompanyId();

        reportRepository.findByIdAndCompanyId(id, companyId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        List<String> indicatorsName =
                valuesIndicatorsFinancialReportsRepository.findIndicatorNamesByReportId(id);

        return new InfoReportDto(indicatorsName.size(), indicatorsName);
    }

    @Transactional
    public void approveForAnalysis(Long id) {
        Long companyId = CustomUserDetails.requireCurrent().getCompanyId();
        Report report = reportRepository.findByIdAndCompanyId(id, companyId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        ReportWorkflow.requireApproveForAnalysis(report.getStatus());

        ReportStatus newStatus = ReportStatus.APPROVED_FOR_ANALYSIS;
        if (report.getStatus() == newStatus) {
            return;
        }

        report.setStatus(newStatus);
        reportRepository.save(report);

        User user = userRepository.findById(CustomUserDetails.requireCurrent().getUserId()).orElseThrow();
        reportAuditService.recordStatusChange(report, user, newStatus);
    }

    @Transactional
    public void calculateIndicators(Long id) {
        Long companyId = CustomUserDetails.requireCurrent().getCompanyId();
        Report report = reportRepository.findByIdAndCompanyId(id, companyId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        ReportWorkflow.requireCalculate(report.getStatus());

        calculatorService.calculateIndicatorsFinancialAnalyze(id);

        ReportStatus newStatus = ReportStatus.CALCULATIONS_COMPLETED;
        if (report.getStatus() == newStatus) {
            return;
        }

        report.setStatus(newStatus);
        reportRepository.save(report);

        User user = userRepository.findById(CustomUserDetails.requireCurrent().getUserId()).orElseThrow();
        reportAuditService.recordStatusChange(report, user, newStatus);
    }

    @Transactional(readOnly = true)
    public String findComment(Long id) {
        Long companyId = CustomUserDetails.requireCurrent().getCompanyId();
        reportRepository.findByIdAndCompanyId(id, companyId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        return commentRepository.findByReport_Id(id)
                .map(Comment::getComment)
                .orElse("");
    }

    @Transactional
    public void saveComment(Long id, String commentText) {
        if (commentText == null || commentText.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Комментарий не может быть пустым");
        }

        Long companyId = CustomUserDetails.requireCurrent().getCompanyId();
        Report report = reportRepository.findByIdAndCompanyId(id, companyId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        ReportWorkflow.requireSaveAnalystComment(report.getStatus());

        Comment comment = commentRepository.findByReport_Id(id)
                .orElseGet(() -> {
                    Comment newComment = new Comment();
                    newComment.setReport(report);
                    return newComment;
                });
        comment.setComment(commentText.trim());
        commentRepository.save(comment);

        if (report.getStatus() == ReportStatus.APPROVED) {
            return;
        }

        ReportStatus newStatus = ReportStatus.COMMENTS_ADDED;
        if (report.getStatus() == newStatus) {
            return;
        }

        report.setStatus(newStatus);
        reportRepository.save(report);

        User user = userRepository.findById(CustomUserDetails.requireCurrent().getUserId()).orElseThrow();
        reportAuditService.recordStatusChange(report, user, newStatus);
    }

    @Transactional
    public void delete(Long id) {
        Report report = requireReportEntity(id);

        String storedFilename = report.getStoredFilename();
        reportRepository.delete(report);
        fileStorageService.delete(storedFilename);
    }

    @Transactional
    public void approveReport(Long id) {
        Long companyId = CustomUserDetails.requireCurrent().getCompanyId();
        Report report = reportRepository.findByIdAndCompanyId(id, companyId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        ReportWorkflow.requireApproveReport(report.getStatus());

        ReportStatus newStatus = ReportStatus.APPROVED;
        if (report.getStatus() == newStatus) {
            return;
        }

        report.setStatus(newStatus);
        reportRepository.save(report);

        User user = userRepository.findById(CustomUserDetails.requireCurrent().getUserId()).orElseThrow();
        reportAuditService.recordStatusChange(report, user, newStatus);
    }
}