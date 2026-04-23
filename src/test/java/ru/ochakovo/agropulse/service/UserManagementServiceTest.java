package ru.ochakovo.finpolicy.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.transaction.annotation.Transactional;
import ru.ochakovo.finpolicy.IntegrationTestBase;
import ru.ochakovo.finpolicy.dto.CreateCompanyDto;
import ru.ochakovo.finpolicy.dto.CreateUserDto;
import ru.ochakovo.finpolicy.dto.UpdateUserDto;
import ru.ochakovo.finpolicy.entity.organization.Role;
import ru.ochakovo.finpolicy.entity.organization.UserStatus;
import ru.ochakovo.finpolicy.repository.CompanyRepository;
import ru.ochakovo.finpolicy.repository.UserRepository;

@WithMockUser(username = "admin@ochakovo.ru", roles = "ADMINISTRATOR")
@Transactional
class UserManagementServiceTest extends IntegrationTestBase {

    @Autowired
    UserManagementService userManagementService;
    @Autowired
    CompanyRepository companyRepository;
    @Autowired
    UserRepository userRepository;

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

}