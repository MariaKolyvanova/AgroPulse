package ru.ochakovo.agropulse.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ochakovo.agropulse.dto.company.CompanyCreateEditDto;
import ru.ochakovo.agropulse.dto.company.CompanyReadDto;
import ru.ochakovo.agropulse.entity.organization.Company;
import ru.ochakovo.agropulse.mapper.company.CompanyCreateEditMapper;
import ru.ochakovo.agropulse.mapper.company.CompanyReadMapper;
import ru.ochakovo.agropulse.repository.CompanyRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class CompanyService {
    private final CompanyRepository companyRepository;
    private final CompanyCreateEditMapper companyCreateEditMapper;
    private final CompanyReadMapper companyReadMapper;

    public CompanyReadDto create(CompanyCreateEditDto companyDto){
        Company company = companyCreateEditMapper.map(companyDto);
        companyRepository.save(company);

        return companyReadMapper.map(company);
    }

    public Optional<CompanyReadDto> findById(Long companyId) {
        return companyRepository.findById(companyId)
                .map(companyReadMapper::map);
    }
}
