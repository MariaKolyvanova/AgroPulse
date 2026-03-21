package ru.ochakovo.finpolicy.dto;

import ru.ochakovo.finpolicy.database.entity.organization.Role;

public record RegisterCompanyDto(
        String companyName
    ) {
}