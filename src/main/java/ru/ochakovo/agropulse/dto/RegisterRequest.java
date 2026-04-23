package ru.ochakovo.agropulse.dto;

public record RegisterRequest(
        CreateCompanyDto company,
        CreateUserDto user
) {
}
