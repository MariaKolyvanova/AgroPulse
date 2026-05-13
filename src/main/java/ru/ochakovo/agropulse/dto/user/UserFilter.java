package ru.ochakovo.agropulse.dto.user;

import ru.ochakovo.agropulse.entity.organization.Role;
import ru.ochakovo.agropulse.entity.organization.UserStatus;

public record UserFilter(
        String lastName,
        String firstName,
        String patronymic,
        Role role,
        UserStatus userStatus
) {
}
