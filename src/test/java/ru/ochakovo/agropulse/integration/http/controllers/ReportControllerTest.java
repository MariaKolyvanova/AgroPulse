package ru.ochakovo.agropulse.integration.http.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import ru.ochakovo.agropulse.IntegrationTestBase;
import ru.ochakovo.agropulse.annotation.WebIT;
import ru.ochakovo.agropulse.repository.ReportRepository;
import ru.ochakovo.agropulse.repository.ValuesIndicatorsFinancialAnalyzeRepository;
import ru.ochakovo.agropulse.repository.ValuesIndicatorsFinancialReportsRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
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
                .andExpect(flash().attributeExists("successMessage"))
                .andReturn()
                .getResponse()
                .getRedirectedUrl();

        Long reportId = Long.parseLong(redirectUrl.replaceAll(".*/reports/(\\d+)", "$1"));

        assertThat(reportRepository.findById(reportId)).isPresent();
        assertThat(valuesReportsRepository.getValueByCode(reportId, "1600")).isEqualTo(10_000_000.0);
        assertThat(valuesReportsRepository.getValueByCode(reportId, "1210")).isEqualTo(800_000.0);
        assertThat(valuesAnalyzeRepository.findByReport_Id(reportId)).isNotEmpty();
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

    private byte[] resourceBytes(String path) throws Exception {
        try (var in = getClass().getClassLoader().getResourceAsStream(path)) {
            if (in == null) {
                throw new IllegalStateException("Resource not found: " + path);
            }
            return in.readAllBytes();
        }
    }
}
