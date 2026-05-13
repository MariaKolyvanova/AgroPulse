package ru.ochakovo.agropulse.dto.user;

import ru.ochakovo.agropulse.entity.organization.Role;
import ru.ochakovo.agropulse.entity.organization.UserStatus;

public record UserReadDto (
        Long id,
        Role role,
        Long companyId,
        String username,
        String password,
        String lastName,
        String firstName,
        String patronymic,
        UserStatus userStatus
) {

    public String fullName() {
        return lastName + " " + firstName +
                (patronymic != null ? " " + patronymic : "");
    }
}
