package ru.ochakovo.agropulse.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuditLogService {
//
//    private final AuditLogRepository auditLogRepository;
//    private final CompanyRepository companyRepository;
//    private final UserRepository userRepository;
//
//    @Transactional
//    public void log(
//            EntityType entityType,
//            String entityName,
//            AuditAction action,
//            String description
//           ) {
//
////        User currentUser = currentUserCompanyService.getCurrentUser();
//
//        AuditLog log = AuditLog.builder()
////                .company(currentUser.getCompany())
//                .company(companyRepository.getCompanyById(1L))
////                .user(currentUser)
//                .userId(1L)
//                .entityType(entityType)
//                .entityName(entityName)
//                .action(action)
//                .description(description)
//                .build();
//
//        auditLogRepository.save(log);
//    }
//
//    public String getLog(AuditLog log) {
//
//        return String.format("id=%s, user=%s, action=%s, entityType=%s, entityId=%s, time=%s",
//                log.getId(),
//                log.getUserId(),
//                log.getAction(), log.getEntityType(), log.getEntityName(), log.getCreatedAt());
//    }

}