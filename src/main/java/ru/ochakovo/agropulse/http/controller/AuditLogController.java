package ru.ochakovo.agropulse.http.controllers;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.ochakovo.agropulse.service.AuditLogService;

@Controller
@RequestMapping("/auditlogs")
@RequiredArgsConstructor
public class AuditLogController {

    private final AuditLogService auditLogService;

    @GetMapping
    public String findAll(Model model) {
//        model.addAttribute("auditlogs", auditLogService.findAll())
        return "user/auditlog";
    }
}
