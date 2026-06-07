package ru.ochakovo.agropulse.http.controller.audit;


import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.ochakovo.agropulse.dto.common.PageResponse;
import ru.ochakovo.agropulse.dto.audit.*;
import ru.ochakovo.agropulse.dto.user.UserReadDto;
import ru.ochakovo.agropulse.entity.data.ReportStatus;
import ru.ochakovo.agropulse.security.CustomUserDetails;
import ru.ochakovo.agropulse.service.audit.AuthAuditService;
import ru.ochakovo.agropulse.service.audit.ReportAuditService;
import ru.ochakovo.agropulse.service.audit.UserAuditService;
import ru.ochakovo.agropulse.service.organization.UserService;
import ru.ochakovo.agropulse.service.report.core.ReportService;

import java.util.Comparator;

@RequestMapping("/audit")
@Controller
@RequiredArgsConstructor
public class AuditController {

    private final AuthAuditService authAuditService;
    private final UserAuditService userAuditService;
    private final ReportAuditService reportAuditService;
    private final UserService userService;
    private final ReportService reportService;

    @GetMapping
    public String audit(Model model, AuthAuditFilter authAuditFilter, UserAuditFilter userAuditFilter,
                        ReportAuditFilter reportAuditFilter, Pageable pageable,
                        @RequestParam(defaultValue = "reports") String tab) {

        model.addAttribute("tab", tab);

        switch (tab) {
            case "reports" -> {
                Long companyId = CustomUserDetails.requireCurrent().getCompanyId();
                Page<ReportAuditReadDto> page = reportAuditService.findAll(reportAuditFilter, pageable);
                model.addAttribute("reportAudits", PageResponse.of(page));
                model.addAttribute("employeeNames", userService.findAll(companyId).stream()
                        .map(UserReadDto::fullName)
                        .sorted(Comparator.naturalOrder())
                        .toList());
                model.addAttribute("reportNames", reportService.findReportNames(companyId));
                model.addAttribute("statuses", ReportStatus.values());
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
