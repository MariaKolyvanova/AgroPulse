package ru.ochakovo.agropulse.dto;

import ru.ochakovo.agropulse.entity.organization.Role;
import ru.ochakovo.agropulse.entity.organization.UserStatus;

public record UpdateUserDto(
        Role role,
        String email,
        String password,
        String fullname,
        UserStatus userStatus
) {
}
