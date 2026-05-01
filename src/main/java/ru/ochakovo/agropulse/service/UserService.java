package ru.ochakovo.agropulse.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ochakovo.agropulse.dto.CreateCompanyDto;
import ru.ochakovo.agropulse.dto.CreateUserDto;
import ru.ochakovo.agropulse.dto.UpdateUserDto;
import ru.ochakovo.agropulse.entity.organization.*;
import ru.ochakovo.agropulse.repository.CompanyRepository;
import ru.ochakovo.agropulse.repository.UserRepository;

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

        auditLogService.log(EntityType.COMPANY, company.getName(), AuditAction.CREATE, "Создана компания " + company.getName());

    }

    @Transactional
    public User createUser(CreateUserDto createUserDto){

        if (userRepository.existsByEmail(createUserDto.email())) {
            throw new IllegalStateException("Логин уже занят");
        }

        User user = userRepository.save(
                User
                        .builder()
                        .email(createUserDto.email())
                        .password(createUserDto.password())
                        .fullName(createUserDto.fullName())
                        .company(currentUserService.getCurrentCompany())
                        .role(createUserDto.role())
                        .userStatus(UserStatus.ACTIVE)
                        .build());

        auditLogService.log(EntityType.USER, user.getFullName(), AuditAction.CREATE, "Создан пользователь " + user.getFullName());
        return user;
    }

    @Transactional
    public User updateUser(Long userId, UpdateUserDto dto){
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
        if (dto.fullName() != null){
            changes += "ФИО: " + user.getFullName() + " -> " + dto.fullName() + "; ";
            user.setFullName(dto.fullName());
        }
        if (dto.userStatus() != null){
            changes += "статус: " + user.getUserStatus() + " -> " + dto.userStatus() + "; ";
            user.setUserStatus(dto.userStatus());
        }

        auditLogService.log(EntityType.USER, user.getFullName(), AuditAction.UPDATE, changes);

        return user;
    }

    public void validateUserExists(Long userId){
        if (!userRepository.existsById(userId)){
            throw new IllegalStateException("Пользователь не найден");
        }
    }
}