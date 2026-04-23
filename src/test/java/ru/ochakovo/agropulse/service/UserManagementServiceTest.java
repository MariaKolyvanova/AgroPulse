package ru.ochakovo.agropulse.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.transaction.annotation.Transactional;
import ru.ochakovo.agropulse.IntegrationTestBase;
import ru.ochakovo.agropulse.dto.*;
import ru.ochakovo.agropulse.entity.organization.AuditLog;
import ru.ochakovo.agropulse.entity.organization.EntityType;
import ru.ochakovo.agropulse.entity.organization.Role;
import ru.ochakovo.agropulse.entity.organization.UserStatus;
import ru.ochakovo.agropulse.mapper.AuditLogMapper;
import ru.ochakovo.agropulse.repository.AuditLogRepository;
import ru.ochakovo.agropulse.repository.CompanyRepository;
import ru.ochakovo.agropulse.repository.UserRepository;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@WithMockUser(username = "admin@ochakovo.ru", roles = "ADMINISTRATOR")
@Transactional
class UserManagementServiceTest extends IntegrationTestBase {

    @Autowired
    UserManagementService userManagementService;
    @Autowired
    CompanyRepository companyRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    AuditLogRepository auditLogRepository;
    @Autowired
    AuditLogService auditLogService;
    @Autowired
    AuditLogMapper mapper;

    @Test
    void createCompany(){
        CreateCompanyDto createCompanyDto = new CreateCompanyDto(
                "Очаково",
                "Саратовская область",
                "Пищевая промышленность"
        );


        CreateUserDto createUserDto = new CreateUserDto(
                "Artem@mail.ru",
                "1234",
                "Артёмов Артём Артёмович",
                Role.OPERATOR,
                UserStatus.ACTIVE
        );

        userManagementService.createCompany(createCompanyDto, createUserDto);

        for (AuditLog log : auditLogRepository.findAllByUserId(3L)){
            System.out.println(auditLogService.getLog(log));
        }

    }

    @Test
    void updateUser(){
        UpdateUserDto updateUserDto = new UpdateUserDto(
                Role.ANALYTIC,
                "23",
                "12",
                "ФИО",
                UserStatus.ARCHIVE
        );
        userManagementService.updateUser(2L, updateUserDto);
    }

    @Test
    void shouldFilterAuditLogsByEntityType() {

        AuditLogFilter filter = new AuditLogFilter(
                null,
                EntityType.REPORT,
                null,
                null,
                null,
                null
        );

        Pageable pageable = PageRequest.of(
                0,
                20,
                Sort.by(Sort.Direction.DESC, "entityName"));

        Page<AuditLogDto> result = auditLogService.findAll(filter, pageable);

        assertNotNull(result);

        assertTrue(
                result.getContent().stream()
                        .allMatch(log -> log.entityType() == EntityType.REPORT)
                  );

        result.getContent().forEach(System.out::println);
    }
}