package ru.ochakovo.agropulse.service.report.xml;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.ochakovo.agropulse.IntegrationTestBase;
import ru.ochakovo.agropulse.annotation.IT;
import ru.ochakovo.agropulse.entity.data.Month;
import ru.ochakovo.agropulse.entity.data.Report;
import ru.ochakovo.agropulse.entity.data.ReportStatus;
import ru.ochakovo.agropulse.entity.organization.Company;
import ru.ochakovo.agropulse.repository.organization.CompanyRepository;
import ru.ochakovo.agropulse.repository.core.ReportRepository;
import ru.ochakovo.agropulse.repository.xml.ValuesIndicatorsFinancialReportsRepository;

import java.io.InputStream;

import static org.assertj.core.api.Assertions.assertThat;

@IT
class FinancialReportIndicatorImportServiceTest extends IntegrationTestBase {

    @Autowired
    FinancialReportIndicatorImportService importService;
    @Autowired
    ReportRepository reportRepository;
    @Autowired
    CompanyRepository companyRepository;
    @Autowired
    ValuesIndicatorsFinancialReportsRepository valuesRepository;

    @Test
    void
    importFromXml_addsZeroForAbsentCodes1260_1430_1530() throws Exception {
        Report report = saveReport();

        try (InputStream xml = resource("xml/sample-balance.xml")) {
            importService.importFromXml(report, xml);
        }

        assertThat(valuesRepository.getValueByCode(report.getId(), "1260")).isEqualTo(0.0);
        assertThat(valuesRepository.getValueByCode(report.getId(), "1430")).isEqualTo(0.0);
        assertThat(valuesRepository.getValueByCode(report.getId(), "1530")).isEqualTo(0.0);
        assertThat(valuesRepository.getValueByCode(report.getId(), "1230")).isEqualTo(1_000_000.0);
        assertThat(valuesRepository.getValueByCode(report.getId(), "1240")).isEqualTo(200_000.0);
        assertThat(valuesRepository.getValueByCode(report.getId(), "1250")).isEqualTo(2_000_000.0);
    }

    @Test
    void importFromXml_addsZeroForAbsentLiquidityDetailLines() throws Exception {
        Report report = saveReport();

        try (InputStream xml = resource("xml/sample-balance-partial.xml")) {
            importService.importFromXml(report, xml);
        }

        assertThat(valuesRepository.getValueByCode(report.getId(), "1230")).isEqualTo(0.0);
        assertThat(valuesRepository.getValueByCode(report.getId(), "1240")).isEqualTo(0.0);
        assertThat(valuesRepository.getValueByCode(report.getId(), "1250")).isEqualTo(0.0);
    }

    @Test
    void importFromXml_persistsParsedReportingValues() throws Exception {
        Report report = saveReport();

        try (InputStream xml = resource("xml/sample-balance.xml")) {
            importService.importFromXml(report, xml);
        }

        assertThat(valuesRepository.getValueByCode(report.getId(), "1210")).isEqualTo(800_000.0);
        assertThat(valuesRepository.getValueByCode(report.getId(), "1600")).isEqualTo(10_000_000.0);
    }

    private Report saveReport() {
        Company company = companyRepository.findById(1L).orElseThrow();
        Report report = Report.builder()
                .company(company)
                .reportYear((short) 2024)
                .reportMonth(Month.DECEMBER)
                .reportName("Тест импорта")
                .status(ReportStatus.UPLOADED)
                .originalFilename("sample-balance.xml")
                .storedFilename("test-" + System.nanoTime() + ".xml")
                .contentType("application/xml")
                .fileSize(1L)
                .build();
        return reportRepository.save(report);
    }

    private InputStream resource(String path) {
        InputStream stream = getClass().getClassLoader().getResourceAsStream(path);
        if (stream == null) {
            throw new IllegalStateException("Resource not found: " + path);
        }
        return stream;
    }
}
