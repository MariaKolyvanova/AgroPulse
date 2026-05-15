package ru.ochakovo.agropulse.http.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.ochakovo.agropulse.dto.PageResponse;
import ru.ochakovo.agropulse.dto.report.ReportCreateDto;
import ru.ochakovo.agropulse.dto.report.ReportFilter;
import ru.ochakovo.agropulse.dto.report.ReportReadDto;
import ru.ochakovo.agropulse.entity.data.ReportStatus;
import ru.ochakovo.agropulse.service.audit.ReportAuditService;
import ru.ochakovo.agropulse.service.report.ReportService;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;

@RequestMapping("/reports")
@Controller
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;
    private final ReportAuditService reportAuditService;

    @GetMapping("/create")
    public String create(){
        return "/reports/create";
    }

    @PostMapping("/create")
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

        return "reports/reports";
    }

    @GetMapping("/{id}")
    public String report(Model model, @PathVariable Long id) {

        model.addAttribute("report", reportService.findById(id));
        model.addAttribute("audits", reportAuditService.findAllById(id));
        model.addAttribute("info", reportService.getInfo(id));

        return "reports/report";
    }

    @GetMapping("calculation")
    public String calculation() {

        return "reports/calculation";
    }

    @GetMapping("/analytic")
    public String analytic() {

        return "reports/analytic";
    }

    @GetMapping("/export")
    public String export() {

        return "reports/export";
    }
}
