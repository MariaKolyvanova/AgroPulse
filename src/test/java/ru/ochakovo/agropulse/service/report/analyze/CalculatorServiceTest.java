package ru.ochakovo.agropulse.service.report.analyze;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.ochakovo.agropulse.IntegrationTestBase;
import ru.ochakovo.agropulse.annotation.IT;
import ru.ochakovo.agropulse.dto.report.core.WarningDto;
import ru.ochakovo.agropulse.entity.data.Month;
import ru.ochakovo.agropulse.entity.data.Report;
import ru.ochakovo.agropulse.entity.data.ReportStatus;
import ru.ochakovo.agropulse.entity.data.ValuesIndicatorsFinancialAnalyze;
import ru.ochakovo.agropulse.entity.organization.Company;
import ru.ochakovo.agropulse.repository.organization.CompanyRepository;
import ru.ochakovo.agropulse.repository.analyze.IndicatorsFinancialAnalyzeRepository;
import ru.ochakovo.agropulse.repository.core.ReportRepository;
import ru.ochakovo.agropulse.repository.analyze.ValuesIndicatorsFinancialAnalyzeRepository;
import ru.ochakovo.agropulse.service.report.xml.FinancialReportIndicatorImportService;
import java.io.InputStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

@IT
class CalculatorServiceTest extends IntegrationTestBase {

    @Autowired
    CalculatorService calculatorService;
    @Autowired
    FinancialReportIndicatorImportService importService;
    @Autowired
    ReportRepository reportRepository;
    @Autowired
    CompanyRepository companyRepository;
    @Autowired
    ValuesIndicatorsFinancialAnalyzeRepository valuesAnalyzeRepository;
    @Autowired
    IndicatorsFinancialAnalyzeRepository indicatorsAnalyzeRepository;

    private Long reportId;

    @BeforeEach
    void setUp() throws Exception {
        Report report = saveReport();
        reportId = report.getId();

        try (InputStream xml = resource("xml/sample-balance.xml")) {
            importService.importFromXml(report, xml);
        }
        calculatorService.calculateIndicatorsFinancialAnalyze(reportId);
    }

    @Test
    void calculate_totalAssetsEqualsBalanceLine1600() {
        assertAnalyzeValue("TOTAL_ASSETS_VALUE", 10_000_000.0);
    }

    @Test
    void calculate_workingCapitalEquals1200Minus1500() {
        assertAnalyzeValue("WORKING_CAPITAL", 2_000_000.0);
    }

    @Test
    void calculate_equityCapitalUsesZeroForAbsentCodes() {
        assertAnalyzeValue("EQUITY_CAPITAL", 5_100_000.0);
    }

    @Test
    void calculate_currentLiquidityRatio() {
        double expected = 6_000_000.0 / 3_900_000.0;
        assertAnalyzeValue("CURRENT_LIQUIDITY_RATIO", expected);
    }

    @Test
    void calculate_quickLiquidityRatio() {
        double expected = 3_200_000.0 / 3_900_000.0;
        assertAnalyzeValue("QUICK_LIQUIDITY_RATIO", expected);
    }

    @Test
    void calculate_absoluteLiquidityRatio() {
        double expected = 2_200_000.0 / 3_900_000.0;
        assertAnalyzeValue("ABSOLUTE_LIQUIDITY_RATIO", expected);
    }

    @Test
    void getWarning_partialXmlReportsMissingIndicators() throws Exception {
        Report partialReport = saveReport();
        try (InputStream xml = resource("xml/sample-balance-partial.xml")) {
            importService.importFromXml(partialReport, xml);
        }

        WarningDto warning = calculatorService.getWarning(partialReport.getId());

        assertThat(warning.missingReportIndicators()).isNotEmpty();
        assertThat(warning.missingAnalyzeIndicators()).isNotEmpty();
        assertThat(warning.missingReportIndicators()).anyMatch(name -> name.toLowerCase().contains("запас"));
    }

    @Test
    void getWarning_fullXml_hasNoMissingReportIndicators() {
        WarningDto warning = calculatorService.getWarning(reportId);

        assertThat(warning.missingReportIndicators()).isEmpty();
    }

    private void assertAnalyzeValue(String indicatorCode, double expected) {
        var indicator = indicatorsAnalyzeRepository.findByCode(indicatorCode).orElseThrow();
        ValuesIndicatorsFinancialAnalyze value = valuesAnalyzeRepository.findByReport_Id(reportId).stream()
                .filter(v -> v.getIndicatorsFinancialAnalyze().getId().equals(indicator.getId()))
                .findFirst()
                .orElseThrow();

        assertThat(value.getValue()).isCloseTo(expected, within(0.01));
    }

    private Report saveReport() {
        Company company = companyRepository.findById(1L).orElseThrow();
        Report report = Report.builder()
                .company(company)
                .reportYear((short) 2024)
                .reportMonth(Month.DECEMBER)
                .reportName("Тест расчёта")
                .status(ReportStatus.UPLOADED)
                .originalFilename("sample-balance.xml")
                .storedFilename("calc-test-" + System.nanoTime() + ".xml")
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
