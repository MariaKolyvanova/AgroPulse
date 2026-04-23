package ru.ochakovo.agropulse.controllers;


import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.ochakovo.agropulse.dto.AuditLogDto;
import ru.ochakovo.agropulse.dto.AuditLogFilter;
import ru.ochakovo.agropulse.service.AuditLogService;

@Controller
@RequestMapping("/api/audit-logs")
@RequiredArgsConstructor
public class AuditLogController {

    private final AuditLogService auditLogService;

    @GetMapping
    public Page<AuditLogDto> findAll(AuditLogFilter filter, Pageable pageable) {
        return auditLogService.findAll(filter, pageable);
    }
}
