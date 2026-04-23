package ru.ochakovo.finpolicy.dto;

import ru.ochakovo.finpolicy.entity.organization.Role;
import ru.ochakovo.finpolicy.entity.organization.UserStatus;

public record CreateUserDto(
        String email,
        String password,
        String fullname,
        Role role,
        UserStatus userStatus
) {
}
