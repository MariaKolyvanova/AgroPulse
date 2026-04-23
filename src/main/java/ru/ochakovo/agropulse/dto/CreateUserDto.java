package ru.ochakovo.agropulse.dto;

import ru.ochakovo.agropulse.entity.organization.Role;
import ru.ochakovo.agropulse.entity.organization.UserStatus;

public record CreateUserDto(
        String email,
        String password,
        String fullname,
        Role role,
        UserStatus userStatus
) {
}
