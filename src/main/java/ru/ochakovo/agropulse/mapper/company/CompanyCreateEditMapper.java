package ru.ochakovo.agropulse.mapper.company;

import org.mapstruct.Mapper;
import ru.ochakovo.agropulse.dto.company.CompanyCreateEditDto;
import ru.ochakovo.agropulse.entity.organization.Company;

@Mapper(componentModel = "spring")
public interface CompanyCreateEditMapper {
    Company map(CompanyCreateEditDto companyCreateEditDto);
}
