package ru.ochakovo.finpolicy.dto;

public record RegisterRequest(
        CreateCompanyDto company,
        CreateUserDto user
) {
}
