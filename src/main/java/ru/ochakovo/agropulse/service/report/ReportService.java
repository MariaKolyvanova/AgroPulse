package ru.ochakovo.agropulse.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import ru.ochakovo.agropulse.dto.report.InfoReportDto;
import ru.ochakovo.agropulse.dto.report.ReportCreateDto;
import ru.ochakovo.agropulse.dto.report.ReportFilter;
import ru.ochakovo.agropulse.dto.report.ReportReadDto;
import ru.ochakovo.agropulse.entity.data.Report;
import ru.ochakovo.agropulse.entity.data.ReportStatus;
import ru.ochakovo.agropulse.entity.organization.User;
import ru.ochakovo.agropulse.mapper.report.ReportCreateMapper;
import ru.ochakovo.agropulse.mapper.report.ReportReadMapper;
import ru.ochakovo.agropulse.querydsl.QPredicates;
import ru.ochakovo.agropulse.repository.*;
import ru.ochakovo.agropulse.service.report.FinancialReportIndicatorImportService;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;
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
    private final ReportAuditService reportAuditService;
    private final UserRepository userRepository;
    private final IndicatorRepository indicatorRepository;
    private final IndicatorValueRepository indicatorValueRepository;

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(".xml");

    public Optional<ReportReadDto> findByIdForCompany(Long id) {

        return reportRepository.findById(id)
                .map(reportReadMapper::map);
    }

    @Transactional
    public ReportReadDto create(MultipartFile multipartFile,
                                ReportCreateDto dto) throws XMLStreamException, IOException {

        validate(multipartFile);

        String originalFilename = StringUtils.cleanPath(multipartFile.getOriginalFilename());

        String extension = resolveExtension(originalFilename);
        String storedFilename = UUID.randomUUID() + extension;

        User user = userRepository.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName()).orElseThrow();
        try {
            fileStorageService.save(multipartFile, storedFilename);

            Report report = reportCreateMapper.map(dto);
            report.setCompany(companyRepository.getCompanyById(user.getCompany().getId()));
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

            reportAuditService.recordStatusChange(report,
                    userRepository.findById(user.getId()).orElseThrow(),
                    ReportStatus.UPLOADED);

            return reportReadMapper.map(report);

        } catch (Exception e) {
            fileStorageService.delete(storedFilename);
            throw e;
        }
    }

    private void validate(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Файл пустой");
        }

        String ext = resolveExtension(file.getOriginalFilename());

        if (!ALLOWED_EXTENSIONS.contains(ext)) {
            throw new IllegalArgumentException("Недопустимый файл");
        }
    }

    private String resolveExtension(String filename) {
        if (filename == null) return "";
        int i = filename.lastIndexOf('.');
        return (i > 0) ? filename.substring(i).toLowerCase() : "";
    }

    public Page<ReportReadDto> findAll(ReportFilter filter, Pageable pageable) {


        Short reportMonth = filter.month() == null ? null : (short) filter.month().getNumber();

        User user = userRepository.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName()).orElseThrow();

        var predicate = QPredicates.builder()
                .add(user.getCompany().getId(), report.company.id::eq)
                .add(filter.year(), report.reportYear::eq)
                .add(reportMonth, report.reportMonth::eq)
                .add(filter.reportName(), report.reportName::containsIgnoreCase)
                .add(filter.status(), report.status::eq)
                .build();

        return reportRepository.findAll(predicate, pageable)
                .map(reportReadMapper::map);
    }

    @Transactional(readOnly = true)
    public InfoReportDto getInfo(Long id) {

        User user = userRepository.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName()).orElseThrow();


        reportRepository.findByIdAndCompany_Id(id, user.getCompany().getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        List<String> indicatorsName =
                indicatorValueRepository.findIndicatorNamesByReportId(id);

        return new InfoReportDto(indicatorsName.size(), indicatorsName);
    }
}