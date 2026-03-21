package ru.ochakovo.finpolicy.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.ochakovo.finpolicy.IntegrationTestBase;
import ru.ochakovo.finpolicy.database.entity.organization.Role;
import ru.ochakovo.finpolicy.database.repository.CompanyRepository;
import ru.ochakovo.finpolicy.database.repository.UserRepository;
import ru.ochakovo.finpolicy.dto.ChangeRoleDto;
import ru.ochakovo.finpolicy.dto.RegisterCompanyDto;
import ru.ochakovo.finpolicy.dto.RegisterUserDto;



class UserManagementServiceTest extends IntegrationTestBase {

    @Autowired
    UserManagementService userManagementService;

    @Autowired
    CompanyRepository companyRepository;

    @Autowired
    UserRepository userRepository;

    @Test
    void checkRegistrationCompany(){
        RegisterCompanyDto registerCompany = new RegisterCompanyDto(
                "Очаково"
        );
        RegisterUserDto  registerUser = new RegisterUserDto(
                "kolyvanovaM",
                "qwerty123",
                "Мария",
                "Колыванова",
                3L,
                Role.ADMIN
        );

        userManagementService.registerCompany(registerCompany, registerUser);
        var company = companyRepository.getCompanyByName("Очаково");

        companyRepository.findAll().forEach(i -> {
            System.out.println(i.getName());
        });
    }

    @Test
    void checkChangeRole(){
        ChangeRoleDto dto = new ChangeRoleDto(2L, Role.OPERATOR);
        userManagementService.changeUserRole(dto);
        System.out.println();
    }

    @Test
    void deleteUser(){
        userManagementService.deleteUser(2L);
        System.out.println();
    }
}