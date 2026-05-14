package ru.ochakovo.agropulse.dto.company;

public record InfoCompanyDto(
        Long reportsSize,
        Long usersSize,
        Long usersActiveSize
) {
}