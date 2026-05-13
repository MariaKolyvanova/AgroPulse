package ru.ochakovo.agropulse.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import ru.ochakovo.agropulse.entity.organization.Role;
import ru.ochakovo.agropulse.entity.organization.UserStatus;

public record UserUpdateDto(
        Role role,

        @Email
        String username,

        @NotBlank
        String password,

        @Size(min = 3, max = 64)
        String lastName,

        @Size(min = 3, max = 64)
        String firstName,

        @Size(min = 3, max = 64)
        String patronymic,

        UserStatus userStatus
) {
}
