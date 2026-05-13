package ru.ochakovo.agropulse.http.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
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
import ru.ochakovo.agropulse.service.UserService;
import ru.ochakovo.agropulse.service.audit.ReportAuditService;
import ru.ochakovo.agropulse.service.report.ReportService;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;


@RequestMapping("/operator")
@Controller
@RequiredArgsConstructor
public class OperatorController {
    private final ReportService reportService;
    private final UserService userService;
    private final ReportAuditService reportAuditService;

    @GetMapping("/create")
    public String create(){
        return "/operator/create";
    }

    @PostMapping("/create")
    public String create(@RequestParam MultipartFile file, @Valid @ModelAttribute ReportCreateDto report,
                         BindingResult bindingResult, Authentication auth, RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("reportErrors", bindingResult.getAllErrors());
            redirectAttributes.addFlashAttribute("report", report);
            return "redirect:/operator/create";
        }

        var currentUser = userService.findByUsername(auth.getName()).orElseThrow();
        ReportReadDto newReport;
        try {
            newReport = reportService.create(file, report);
            redirectAttributes.addFlashAttribute("successMessage", "Отчет успешно загружен");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("createError", ex.getMessage());
            redirectAttributes.addFlashAttribute("report", report);
            return "redirect:/operator/create";
        } catch (XMLStreamException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return "redirect:/operator/reports/" + newReport.id();
    }

    @GetMapping("/reports")
    public String reports(Model model, ReportFilter reportFilter, Pageable pageable) {

        Page<ReportReadDto> page = reportService.findAll(reportFilter, pageable);
        model.addAttribute("reports", PageResponse.of(page));
        model.addAttribute("reportFilter", reportFilter);
        model.addAttribute("statuses", ReportStatus.values());

        return "general/reports";
    }

    @GetMapping("/reports/{id}")
    public String report(Model model, @PathVariable Long id, Authentication auth) {

        model.addAttribute("report", reportService.findByIdForCompany(id));
        model.addAttribute("audits", reportAuditService.findAllById(id));
        model.addAttribute("info", reportService.getInfo(id));

        return "/general/report";
    }
}