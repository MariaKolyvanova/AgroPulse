package ru.ochakovo.agropulse.mapper;

import org.mapstruct.Mapper;
import ru.ochakovo.agropulse.dto.CompanyCreateEditDto;
import ru.ochakovo.agropulse.entity.organization.Company;

@Mapper(componentModel = "spring")
public interface CompanyMapper {
    Company map(CompanyCreateEditDto companyCreateEditDto);
}
