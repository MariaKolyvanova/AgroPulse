package ru.ochakovo.agropulse.dto;

import ru.ochakovo.agropulse.entity.organization.Role;
import ru.ochakovo.agropulse.entity.organization.UserStatus;

public record UserFilter(
        String lastName,
        String firstName,
        String patronymic,
        Long companyId,
        Role role,
        UserStatus userStatus
) {
}
