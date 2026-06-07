package ru.ochakovo.agropulse.http.controller.report;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.ochakovo.agropulse.dto.common.PageResponse;
import ru.ochakovo.agropulse.dto.report.core.ReportActionsDto;
import ru.ochakovo.agropulse.dto.report.core.ReportCreateDto;
import ru.ochakovo.agropulse.dto.report.core.ReportFilter;
import ru.ochakovo.agropulse.dto.report.core.ReportReadDto;
import ru.ochakovo.agropulse.dto.report.print.ReportPdfExportOptions;
import ru.ochakovo.agropulse.entity.data.ReportStatus;
import ru.ochakovo.agropulse.service.audit.ReportAuditService;
import ru.ochakovo.agropulse.service.report.analyze.CalculatorService;
import ru.ochakovo.agropulse.service.report.analyze.FinancialAnalyzeService;
import ru.ochakovo.agropulse.service.report.core.ReportService;
import ru.ochakovo.agropulse.service.report.core.ReportWorkflow;
import ru.ochakovo.agropulse.service.report.data.ValuesIndicatorsFinancialReportsService;
import ru.ochakovo.agropulse.service.report.print.ReportPdfService;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.util.List;

@RequestMapping("/reports")
@Controller
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;
    private final ReportAuditService reportAuditService;
    private final CalculatorService calculatorService;
    private final FinancialAnalyzeService financialAnalyzeService;
    private final ValuesIndicatorsFinancialReportsService valuesIndicatorsFinancialReportsService;
    private final ReportPdfService reportPdfService;

    @GetMapping("/create")
    @PreAuthorize("hasAuthority('OPERATOR')")
    public String create() {
        return "reports/core/create";
    }

    @PostMapping("/create")
    @PreAuthorize("hasAuthority('OPERATOR')")
    public String create(@RequestParam MultipartFile file, @Valid @ModelAttribute ReportCreateDto report,
                         BindingResult bindingResult, RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("reportErrors", bindingResult.getAllErrors());
            redirectAttributes.addFlashAttribute("report", report);
            return "redirect:/reports/create";
        }

        ReportReadDto newReport;
        try {
            newReport = reportService.create(file, report);
            redirectAttributes.addFlashAttribute("successMessage", "Отчет успешно загружен");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("createError", ex.getMessage());
            redirectAttributes.addFlashAttribute("report", report);
            return "redirect:/reports/create";
        } catch (XMLStreamException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return "redirect:/reports/" + newReport.id();
    }

    @GetMapping
    public String reports(Model model, ReportFilter reportFilter, Pageable pageable) {

        Page<ReportReadDto> page = reportService.findAll(reportFilter, pageable);
        model.addAttribute("reports", PageResponse.of(page));
        model.addAttribute("reportFilter", reportFilter);
        model.addAttribute("statuses", ReportStatus.values());

        return "reports/core/reports";
    }

    @GetMapping("/{id}")
    public String report(Model model, @PathVariable Long id) {
        ReportReadDto report = reportService.findById(id);

        model.addAttribute("report", report);
        model.addAttribute("audits", reportAuditService.findAllById(id));
        model.addAttribute("info", reportService.getInfo(id));
        model.addAttribute("reportActions", ReportActionsDto.from(report.status()));

        return "reports/core/report";
    }

    @GetMapping("calculation")
    public String calculation() {
        return "redirect:/reports";
    }

    @GetMapping("/analytic")
    public String analyticMenu() {
        return "redirect:/reports";
    }

    @GetMapping("/{id}/analytic")
    @PreAuthorize("hasAuthority('ANALYTIC')")
    public String analytic(Model model, @PathVariable Long id) {
        ReportReadDto report = reportService.findById(id);
        reportService.requireOpenAnalysis(id);

        model.addAttribute("report", report);
        model.addAttribute("canSaveComment", ReportWorkflow.canSaveAnalystComment(report.status()));
        model.addAttribute("analyzePage", financialAnalyzeService.buildAnalyzePage(id));

        return "reports/analyze/analytic";
    }

    @PostMapping("/{id}/analytic")
    @PreAuthorize("hasAuthority('ANALYTIC')")
    public String saveAnalyticComment(@PathVariable Long id,
                                      @RequestParam String comment,
                                      RedirectAttributes redirectAttributes) {
        reportService.saveComment(id, comment);
        redirectAttributes.addFlashAttribute("successMessage", "Комментарий сохранён");
        return "redirect:/reports/" + id + "/analytic";
    }

    @GetMapping("/{id}/export")
    public String export(Model model, @PathVariable Long id) {
        reportService.requireExportPdf(id);
        ReportReadDto report = reportService.findById(id);

        model.addAttribute("report", report);
        model.addAttribute("pdfFileName", ReportPdfService.sanitizeFileName(report.reportName(), id));

        return "reports/print/export";
    }

    @GetMapping("/{id}/print")
    public String print(Model model, @PathVariable Long id) {
        reportService.requireExportPdf(id);
        model.addAttribute("report", reportService.findById(id));
        model.addAttribute("analyzePage", financialAnalyzeService.buildAnalyzePage(id));

        return "reports/print/print";
    }

    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> downloadPdf(
            @PathVariable Long id,
            @RequestParam(defaultValue = "true") boolean esg,
            @RequestParam(defaultValue = "true") boolean tables,
            @RequestParam(defaultValue = "true") boolean charts,
            @RequestParam(defaultValue = "true") boolean recommendations,
            @RequestParam(defaultValue = "true") boolean comment,
            HttpServletRequest request
    ) {
        ReportReadDto report = reportService.findById(id);
        ReportPdfExportOptions options = new ReportPdfExportOptions(esg, tables, charts, recommendations, comment);
        byte[] pdf = reportPdfService.generatePdf(id, options, request);
        String fileName = ReportPdfService.sanitizeFileName(report.reportName(), id);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, ReportPdfService.contentDispositionValue(fileName))
                .contentType(MediaType.APPLICATION_PDF)
                .contentLength(pdf.length)
                .body(pdf);
    }

    @GetMapping("/{id}/data")
    @PreAuthorize("hasAuthority('OPERATOR')")
    public String data(Model model, @PathVariable Long id) {
        ReportReadDto report = reportService.findById(id);
        reportService.requirePrepare(id);

        model.addAttribute("reportId", id);
        model.addAttribute("canReplaceXml", ReportWorkflow.canReplaceXml(report.status()));
        model.addAttribute("warning", calculatorService.getWarning(id));
        model.addAttribute("indicators", valuesIndicatorsFinancialReportsService.findAllById(id));

        return "reports/data/data";
    }

    @PostMapping("/{id}/replace-xml")
    @PreAuthorize("hasAuthority('OPERATOR')")
    public String replaceXml(@PathVariable Long id,
                             @RequestParam MultipartFile file,
                             RedirectAttributes redirectAttributes) {
        try {
            reportService.replaceXmlFile(id, file);
            redirectAttributes.addFlashAttribute("successMessage", "Документ обновлён");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("replaceXmlError", ex.getMessage());
        } catch (XMLStreamException | IOException ex) {
            throw new RuntimeException(ex);
        }
        return "redirect:/reports/" + id + "/data";
    }

    @PostMapping("/{id}/calculate")
    @PreAuthorize("hasAuthority('ANALYTIC')")
    public String calculate(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        reportService.calculateIndicators(id);
        redirectAttributes.addFlashAttribute("successMessage", "Расчёты выполнены");
        return "redirect:/reports/" + id;
    }

    @PostMapping("/{id}/approve-for-analysis")
    @PreAuthorize("hasAuthority('OPERATOR')")
    public String approveForAnalysis(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        reportService.approveForAnalysis(id);
        redirectAttributes.addFlashAttribute("successMessage", "Отчёт утверждён к анализу");
        return "redirect:/reports/" + id;
    }

    @PostMapping("/{id}/approve")
    @PreAuthorize("hasAuthority('ANALYTIC')")
    public String approveReport(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        reportService.approveReport(id);
        redirectAttributes.addFlashAttribute("successMessage", "Отчёт одобрен");
        return "redirect:/reports/" + id;
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        reportService.delete(id);
        return "redirect:/reports";
    }
}
