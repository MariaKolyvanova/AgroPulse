package ru.ochakovo.finpolicy.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ochakovo.finpolicy.database.entity.organization.Company;
import ru.ochakovo.finpolicy.database.entity.organization.Role;
import ru.ochakovo.finpolicy.database.entity.organization.User;
import ru.ochakovo.finpolicy.database.repository.CompanyRepository;
import ru.ochakovo.finpolicy.database.repository.UserRepository;
import ru.ochakovo.finpolicy.dto.ChangeRoleDto;
import ru.ochakovo.finpolicy.dto.RegisterCompanyDto;
import ru.ochakovo.finpolicy.dto.RegisterUserDto;

@RequiredArgsConstructor
@Service
public class UserManagementService {

    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;


    @Transactional
    public void registerCompany(RegisterCompanyDto registerCompany, RegisterUserDto registerUser){

        if (companyRepository.existsByName(registerCompany.companyName())){
            throw new IllegalStateException("Компания уже существует");
        }

        Company company = companyRepository.save(
                Company
                .builder()
                .name(registerCompany.companyName())
                .build());

        createUser(registerUser, company, Role.ADMIN);
    }

    @Transactional
    public void registerUser(RegisterUserDto registerUser) {

        if (userRepository.existsByUsername(registerUser.username())) {
            throw new IllegalStateException("Логин уже занят");
        }

        Company company = companyRepository.findById(registerUser.companyId())
                .orElseThrow(() -> new IllegalStateException("Компания не найдена"));

        createUser(registerUser, company, registerUser.role());
    }

    private void createUser(RegisterUserDto registerUser, Company company, Role role){
        userRepository.save(
                User
                        .builder()
                        .username(registerUser.username())
                        .password(registerUser.password()) // TODO: Spring Security
                        .firstname(registerUser.firstname())
                        .lastname(registerUser.lastname())
                        .company(company)
                        .role(role)
                        .build());
    }

    @Transactional
    public void changeUserRole(ChangeRoleDto dto){

        User user = userRepository.findById(dto.userId())
                .orElseThrow(() -> new IllegalStateException("Пользователь не найден"));

        user.setRole(dto.role());
    }

    @Transactional
    public void deleteUser(Long userId){
        if (!userRepository.existsById(userId)){
            throw new IllegalStateException("Пользователь не найден");
        }

        userRepository.deleteById(userId);
    }

}