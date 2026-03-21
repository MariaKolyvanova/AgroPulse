package ru.ochakovo.finpolicy.dto;

import ru.ochakovo.finpolicy.database.entity.organization.Role;

public record ChangeRoleDto(
        Long userId,
        Role role
) {
}
