package ru.ochakovo.agropulse.mapper.company;

import org.mapstruct.Mapper;
import ru.ochakovo.agropulse.dto.company.CompanyReadDto;
import ru.ochakovo.agropulse.entity.organization.Company;

@Mapper(componentModel = "spring")
public interface CompanyReadMapper {
    CompanyReadDto map(Company company);
}
