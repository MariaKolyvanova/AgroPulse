package ru.ochakovo.agropulse.dto.company;

public record CompanyCreateEditDto(
        String name,
        String region,
        String industry
    ) {
}