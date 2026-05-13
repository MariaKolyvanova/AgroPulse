package ru.ochakovo.agropulse.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import ru.ochakovo.agropulse.entity.organization.Role;
import ru.ochakovo.agropulse.entity.organization.UserStatus;
import ru.ochakovo.agropulse.validation.UserInfo;

@UserInfo
public record UserCreateDto(
        Role role,

        @Email
        String username,

        UserStatus userStatus,

        @NotBlank
        String password,

        @Size(min = 3, max = 64)
        String lastName,

        @Size(min = 3, max = 64)
        String firstName,

        @Size(min = 3, max = 64)
        String patronymic
) {
}
