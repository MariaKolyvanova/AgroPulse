package ru.ochakovo.agropulse.dto;

public record CompanyCreateEditDto(
        String name,
        String region,
        String industry
    ) {
}