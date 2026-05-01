package ru.ochakovo.agropulse.dto;

import ru.ochakovo.agropulse.entity.organization.Role;

public record CreateUserDto(
        String email,
        String password,
        String fullName,
        Role role
) {
}
