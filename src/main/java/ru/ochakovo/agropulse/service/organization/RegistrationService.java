package ru.ochakovo.agropulse.service.organization;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ochakovo.agropulse.dto.company.CompanyCreateEditDto;
import ru.ochakovo.agropulse.dto.user.UserCreateDto;
import ru.ochakovo.agropulse.mapper.company.CompanyCreateEditMapper;
import ru.ochakovo.agropulse.mapper.user.UserCreateMapper;

@Service
@RequiredArgsConstructor
@Transactional
public class RegistrationService {

    private final UserService userService;
    private final CompanyService companyService;
    private final UserCreateMapper userCreateMapper;
    private final CompanyCreateEditMapper companyCreateEditMapper;

    public void register(CompanyCreateEditDto companyDto, UserCreateDto userDto) {
        Long companyId = companyService.create(companyDto).id();
        userService.create(userDto, companyId);
    }
}
