package ru.ochakovo.finpolicy.dto;

public record RegisterRequest(
        RegisterCompanyDto company,
        RegisterUserDto user
) {
}
