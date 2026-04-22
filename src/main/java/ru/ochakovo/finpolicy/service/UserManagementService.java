package ru.ochakovo.finpolicy.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ochakovo.finpolicy.dto.CreateCompanyDto;
import ru.ochakovo.finpolicy.dto.CreateUserDto;
import ru.ochakovo.finpolicy.dto.UpdateUserDto;
import ru.ochakovo.finpolicy.entity.organization.AuditAction;
import ru.ochakovo.finpolicy.entity.organization.Company;
import ru.ochakovo.finpolicy.entity.organization.EntityType;
import ru.ochakovo.finpolicy.entity.organization.User;
import ru.ochakovo.finpolicy.repository.CompanyRepository;
import ru.ochakovo.finpolicy.repository.UserRepository;

@RequiredArgsConstructor
@Service
public class UserManagementService {

    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;
    private final AuditLogService auditLogService;
    private final CurrentUserService currentUserService;

    @Transactional
    public void createCompany(CreateCompanyDto createCompanyDto, CreateUserDto createUserDto){

        if (companyRepository.existsByName(createCompanyDto.companyName())){
            throw new IllegalStateException("Компания уже существует");
        }

        Company company = companyRepository.save(
                Company
                .builder()
                .name(createCompanyDto.companyName())
                .region(createCompanyDto.region())
                .industry(createCompanyDto.industry())
                .build());

        createUser(createUserDto);

        auditLogService.log(EntityType.COMPANY, company.getName(), AuditAction.CREATE, "Создана компания" + company.getName());

    }

    @Transactional
    public void createUser(CreateUserDto createUserDto){

        if (userRepository.existsByEmail(createUserDto.email())) {
            throw new IllegalStateException("Логин уже занят");
        }

        User user = userRepository.save(
                User
                        .builder()
                        .email(createUserDto.email())
                        .password(createUserDto.password())
                        .fullName(createUserDto.fullname())
                        .company(currentUserService.getCurrentCompany())
                        .role(createUserDto.role())
                        .userStatus(createUserDto.userStatus())
                        .build());

        auditLogService.log(EntityType.USER, user.getFullName(), AuditAction.CREATE, "Создан пользователь" + user.getFullName());
    }

    @Transactional
    public void updateUser(Long userId, UpdateUserDto dto){
        validateUserExists(userId);

        User user = userRepository.findUserById(userId);

        String changes = "";

        if (dto.role() != null){
            changes += "роль: " + user.getRole() + " -> " + dto.role() + "; ";
            user.setRole(dto.role());
        }
        if (dto.email() != null){
            changes += "email: " + user.getEmail() + " -> " + dto.email() + "; ";
            user.setEmail(dto.email());
        }
        if (dto.password() != null){
            changes += "пароль изменён; ";
            user.setPassword(dto.password());
        }
        if (dto.fullname() != null){
            changes += "ФИО: " + user.getFullName() + " -> " + dto.fullname() + "; ";
            user.setFullName(dto.fullname());
        }
        if (dto.userStatus() != null){
            changes += "статус: " + user.getUserStatus() + " -> " + dto.userStatus() + "; ";
            user.setUserStatus(dto.userStatus());
        }

        auditLogService.log(EntityType.USER, user.getFullName(), AuditAction.UPDATE, changes);
    }

    public void validateUserExists(Long userId){
        if (!userRepository.existsById(userId)){
            throw new IllegalStateException("Пользователь не найден");
        }
    }
}