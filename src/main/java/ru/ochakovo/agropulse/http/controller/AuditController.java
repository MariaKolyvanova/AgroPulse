package ru.ochakovo.agropulse.http.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.ochakovo.agropulse.dto.PageResponse;
import ru.ochakovo.agropulse.dto.audit.*;
import ru.ochakovo.agropulse.service.audit.AuthAuditService;
import ru.ochakovo.agropulse.service.audit.ReportAuditService;
import ru.ochakovo.agropulse.service.audit.UserAuditService;

@RequestMapping("/audit")
@Controller
@RequiredArgsConstructor
public class AuditController {

    private final AuthAuditService authAuditService;
    private final UserAuditService userAuditService;
    private final ReportAuditService reportAuditService;

    @GetMapping
    public String audit(Model model, AuthAuditFilter authAuditFilter, UserAuditFilter userAuditFilter,
                        ReportAuditFilter reportAuditFilter, Pageable pageable,
                        @RequestParam(defaultValue = "reports") String tab) {

        model.addAttribute("tab", tab);

        switch (tab) {
            case "reports" -> {
                Page<ReportAuditReadDto> page = reportAuditService.findAll(reportAuditFilter, pageable);
                model.addAttribute("reportAudits", PageResponse.of(page));
            }
            case "auth" -> {
                Page<AuthAuditReadDto> page = authAuditService.findAll(authAuditFilter, pageable);
                model.addAttribute("authAudits", PageResponse.of(page));
            }
            case "users" -> {
                Page<UserAuditReadDto> page = userAuditService.findAll(userAuditFilter, pageable);
                model.addAttribute("userAudits", PageResponse.of(page));
            }
        }
        model.addAttribute("reportAuditFilter", reportAuditFilter);
        model.addAttribute("authAuditFilter", authAuditFilter);
        model.addAttribute("userAuditFilter", userAuditFilter);

        return "audit/audit";
    }

}
