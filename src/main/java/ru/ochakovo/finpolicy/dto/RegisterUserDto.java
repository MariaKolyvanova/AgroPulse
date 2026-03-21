package ru.ochakovo.finpolicy.dto;

import ru.ochakovo.finpolicy.database.entity.organization.Company;
import ru.ochakovo.finpolicy.database.entity.organization.Role;

public record RegisterUserDto(
        String username,
        String password,
        String firstname,
        String lastname,
        Long companyId,
        Role role
) {
}
