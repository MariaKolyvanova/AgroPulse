package ru.ochakovo.agropulse.service.organization;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ochakovo.agropulse.dto.company.CompanyCreateEditDto;
import ru.ochakovo.agropulse.dto.company.CompanyReadDto;
import ru.ochakovo.agropulse.dto.company.InfoCompanyDto;
import ru.ochakovo.agropulse.entity.organization.Company;
import ru.ochakovo.agropulse.mapper.company.CompanyCreateEditMapper;
import ru.ochakovo.agropulse.mapper.company.CompanyReadMapper;
import ru.ochakovo.agropulse.repository.organization.CompanyRepository;
import ru.ochakovo.agropulse.security.CustomUserDetails;

@Service
@RequiredArgsConstructor
@Transactional
public class CompanyService {
    private final CompanyRepository companyRepository;
    private final CompanyCreateEditMapper companyCreateEditMapper;
    private final CompanyReadMapper companyReadMapper;

    public CompanyReadDto create(CompanyCreateEditDto companyDto) {
        Company company = companyCreateEditMapper.map(companyDto);
        companyRepository.save(company);

        return companyReadMapper.map(company);
    }

    public CompanyReadDto find() {
        Long companyId = CustomUserDetails.requireCurrent().getCompanyId();

        return companyRepository.findById(companyId).map(companyReadMapper::map).orElseThrow();
    }

    public InfoCompanyDto getInfo() {
        Long companyId = CustomUserDetails.requireCurrent().getCompanyId();

        return companyRepository.getInfoByCompany(companyId);
    }
}
