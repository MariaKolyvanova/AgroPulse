package ru.ochakovo.agropulse.integration.http.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import ru.ochakovo.agropulse.IntegrationTestBase;
import ru.ochakovo.agropulse.annotation.WebIT;
import ru.ochakovo.agropulse.entity.data.Month;
import ru.ochakovo.agropulse.entity.data.Report;
import ru.ochakovo.agropulse.entity.data.ReportStatus;
import ru.ochakovo.agropulse.entity.organization.Company;
import ru.ochakovo.agropulse.repository.organization.CompanyRepository;
import ru.ochakovo.agropulse.repository.core.ReportRepository;
import ru.ochakovo.agropulse.repository.esg.ValuesIndicatorsEsgRepository;
import ru.ochakovo.agropulse.repository.analyze.ValuesIndicatorsFinancialAnalyzeRepository;
import ru.ochakovo.agropulse.repository.xml.ValuesIndicatorsFinancialReportsRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebIT
class ReportControllerTest extends IntegrationTestBase {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ReportRepository reportRepository;
    @Autowired
    ValuesIndicatorsFinancialReportsRepository valuesReportsRepository;
    @Autowired
    ValuesIndicatorsFinancialAnalyzeRepository valuesAnalyzeRepository;
    @Autowired
    ValuesIndicatorsEsgRepository valuesEsgRepository;
    @Autowired
    CompanyRepository companyRepository;

    @Test
    @WithUserDetails("operator_1@ochakovo.ru")
    void create_uploadsXmlAndPersistsIndicatorValues() throws Exception {
        byte[] xml = resourceBytes("xml/sample-balance.xml");

        String redirectUrl = mockMvc.perform(multipart("/reports/create")
                        .file(new MockMultipartFile(
                                "file",
                                "sample-balance.xml",
                                "application/xml",
                                xml))
                        .param("reportYear", "2024")
                        .param("reportMonth", "DECEMBER")
                        .param("reportName", "Интеграционный тест отчёта")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/reports/{\\d+}"))
                .andReturn()
                .getResponse()
                .getRedirectedUrl();

        Long reportId = Long.parseLong(redirectUrl.replaceAll(".*/reports/(\\d+)", "$1"));

        assertThat(reportRepository.findById(reportId)).isPresent();
        assertThat(valuesReportsRepository.getValueByCode(reportId, "1600")).isEqualTo(10_000_000.0);
        assertThat(valuesReportsRepository.getValueByCode(reportId, "1210")).isEqualTo(800_000.0);
        assertThat(valuesAnalyzeRepository.findByReport_Id(reportId)).isEmpty();
    }

    @Test
    void calculate_persistsAnalyzeValuesAndUpdatesStatus() throws Exception {
        byte[] xml = resourceBytes("xml/sample-balance.xml");

        String redirectUrl = mockMvc.perform(multipart("/reports/create")
                        .file(new MockMultipartFile(
                                "file",
                                "sample-balance.xml",
                                "application/xml",
                                xml))
                        .param("reportYear", "2024")
                        .param("reportMonth", "DECEMBER")
                        .param("reportName", "Отчёт для расчёта")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/reports/{\\d+}"))
                .andReturn()
                .getResponse()
                .getRedirectedUrl();

        Long reportId = Long.parseLong(redirectUrl.replaceAll(".*/reports/(\\d+)", "$1"));

        mockMvc.perform(post("/reports/{id}/approve-for-analysis", reportId).with(csrf()))
                .andExpect(status().is3xxRedirection());

        mockMvc.perform(post("/reports/{id}/calculate", reportId).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/reports/" + reportId));

        assertThat(valuesAnalyzeRepository.findByReport_Id(reportId)).isNotEmpty();

        Report report = reportRepository.findById(reportId).orElseThrow();
        assertThat(report.getStatus()).isEqualTo(ReportStatus.CALCULATIONS_COMPLETED);
    }

    @Test
    @WithUserDetails("analyst_expert@ochakovo.ru")
    void calculate_beforeApproval_returnsConflict() throws Exception {
        Report report = saveReport();

        mockMvc.perform(post("/reports/{id}/calculate", report.getId()).with(csrf()))
                .andExpect(status().isConflict());
    }

    @Test
    @WithUserDetails("operator_1@ochakovo.ru")
    void saveEsg_persistsValues() throws Exception {
        saveReport();

        mockMvc.perform(post("/esg")
                        .param("year", "2024")
                        .param("indicatorId", "1", "2")
                        .param("value", "1500.5", "42")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/esg?year=2024"));

        assertThat(valuesEsgRepository.findByCompanyIdAndReportYearWithIndicators(1L, (short) 2024)).hasSize(2);
        assertThat(valuesEsgRepository.findByCompanyIdAndReportYearWithIndicators(1L, (short) 2024).getFirst().getValue())
                .isEqualTo(1500.5);
    }

    @Test
    @WithUserDetails("operator_1@ochakovo.ru")
    void create_allowsEmptyReportMonth() throws Exception {
        byte[] xml = resourceBytes("xml/sample-balance.xml");

        String redirectUrl = mockMvc.perform(multipart("/reports/create")
                        .file(new MockMultipartFile(
                                "file",
                                "sample-balance.xml",
                                "application/xml",
                                xml))
                        .param("reportYear", "2024")
                        .param("reportMonth", "")
                        .param("reportName", "Отчёт без месяца")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/reports/{\\d+}"))
                .andReturn()
                .getResponse()
                .getRedirectedUrl();

        Long reportId = Long.parseLong(redirectUrl.replaceAll(".*/reports/(\\d+)", "$1"));

        Report report = reportRepository.findById(reportId).orElseThrow();
        assertThat(report.getReportMonth()).isNull();
    }

    @Test
    @WithUserDetails("operator_1@ochakovo.ru")
    void create_rejectsEmptyReportName() throws Exception {
        byte[] xml = resourceBytes("xml/sample-balance.xml");

        mockMvc.perform(multipart("/reports/create")
                        .file(new MockMultipartFile(
                                "file",
                                "sample-balance.xml",
                                "application/xml",
                                xml))
                        .param("reportYear", "2024")
                        .param("reportMonth", "DECEMBER")
                        .param("reportName", " ")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/reports/create"))
                .andExpect(flash().attributeExists("reportErrors"));
    }

    @Test
    @WithUserDetails("operator_1@ochakovo.ru")
    void create_rejectsMissingFile() throws Exception {
        mockMvc.perform(multipart("/reports/create")
                        .param("reportYear", "2024")
                        .param("reportMonth", "DECEMBER")
                        .param("reportName", "Отчёт без файла")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/reports/create"))
                .andExpect(flash().attribute("createError", "Выберите xml-файл"));
    }

    @Test
    @WithUserDetails("operator_1@ochakovo.ru")
    void saveEsg_allowsEmptyValues() throws Exception {
        saveReport();

        mockMvc.perform(post("/esg")
                        .param("year", "2024")
                        .param("indicatorId", "1", "2")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/esg?year=2024"));

        assertThat(valuesEsgRepository.findByCompanyIdAndReportYearWithIndicators(1L, (short) 2024)).isEmpty();
        assertThat(reportRepository.findAll().getFirst().getStatus()).isEqualTo(ReportStatus.UPLOADED);
    }

    @Test
    @WithUserDetails("operator_1@ochakovo.ru")
    void saveEsg_rejectsNegativeValue() throws Exception {
        saveReport();

        mockMvc.perform(post("/esg")
                        .param("year", "2024")
                        .param("indicatorId", "1")
                        .param("value", "-5")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/esg?year=2024"))
                .andExpect(flash().attributeExists("esgError"));

        assertThat(valuesEsgRepository.findByCompanyIdAndReportYearWithIndicators(1L, (short) 2024)).isEmpty();
    }

    @Test
    @WithUserDetails("operator_1@ochakovo.ru")
    void create_rejectsNonXmlFile() throws Exception {
        mockMvc.perform(multipart("/reports/create")
                        .file(new MockMultipartFile(
                                "file",
                                "report.txt",
                                "text/plain",
                                "not xml".getBytes()))
                        .param("reportYear", "2024")
                        .param("reportMonth", "DECEMBER")
                        .param("reportName", "Некорректный файл")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/reports/create"))
                .andExpect(flash().attributeExists("createError"));
    }

    @Test
    @WithUserDetails("operator_1@ochakovo.ru")
    void replaceXml_updatesFinancialIndicators() throws Exception {
        byte[] xml = resourceBytes("xml/sample-balance.xml");

        String redirectUrl = mockMvc.perform(multipart("/reports/create")
                        .file(new MockMultipartFile(
                                "file",
                                "sample-balance.xml",
                                "application/xml",
                                xml))
                        .param("reportYear", "2024")
                        .param("reportMonth", "DECEMBER")
                        .param("reportName", "Отчёт для замены XML")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andReturn()
                .getResponse()
                .getRedirectedUrl();

        Long reportId = Long.parseLong(redirectUrl.replaceAll(".*/reports/(\\d+)", "$1"));
        Double valueBefore = valuesReportsRepository.getValueByCode(reportId, "1600");

        mockMvc.perform(multipart("/reports/{id}/replace-xml", reportId)
                        .file(new MockMultipartFile(
                                "file",
                                "sample-balance.xml",
                                "application/xml",
                                xml))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/reports/" + reportId + "/data"));

        Double valueAfter = valuesReportsRepository.getValueByCode(reportId, "1600");
        assertThat(valueAfter).isEqualTo(valueBefore);

        Report updatedReport = reportRepository.findById(reportId).orElseThrow();
        assertThat(updatedReport.getStatus()).isEqualTo(ReportStatus.XML_REPLACED);
    }

    @Test
    @WithUserDetails("operator_1@ochakovo.ru")
    void replaceXml_rejectsNonXmlFile() throws Exception {
        Report report = saveReport();

        mockMvc.perform(multipart("/reports/{id}/replace-xml", report.getId())
                        .file(new MockMultipartFile(
                                "file",
                                "report.txt",
                                "text/plain",
                                "not xml".getBytes()))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/reports/" + report.getId() + "/data"))
                .andExpect(flash().attributeExists("replaceXmlError"));
    }

    @Test
    @WithUserDetails("operator_1@ochakovo.ru")
    void delete_removesReport() throws Exception {
        Report report = saveReport();

        mockMvc.perform(post("/reports/{id}/delete", report.getId()).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/reports"));

        assertThat(reportRepository.findById(report.getId())).isEmpty();
    }

    private Report saveReport() {
        Company company = companyRepository.findById(1L).orElseThrow();
        Report report = Report.builder()
                .company(company)
                .reportYear((short) 2024)
                .reportMonth(Month.DECEMBER)
                .reportName("Тест ESG")
                .status(ReportStatus.UPLOADED)
                .originalFilename("test.xml")
                .storedFilename("test-" + System.nanoTime() + ".xml")
                .contentType("application/xml")
                .fileSize(1L)
                .build();
        return reportRepository.save(report);
    }

    private byte[] resourceBytes(String path) throws Exception {
        try (var in = getClass().getClassLoader().getResourceAsStream(path)) {
            if (in == null) {
                throw new IllegalStateException("Resource not found: " + path);
            }
            return in.readAllBytes();
        }
    }
}
