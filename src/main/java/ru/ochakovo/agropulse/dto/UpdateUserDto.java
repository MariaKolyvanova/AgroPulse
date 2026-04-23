package ru.ochakovo.finpolicy.dto;

import ru.ochakovo.finpolicy.entity.organization.Role;
import ru.ochakovo.finpolicy.entity.organization.UserStatus;

public record UpdateUserDto(
        Role role,
        String email,
        String password,
        String fullname,
        UserStatus userStatus
) {
}
