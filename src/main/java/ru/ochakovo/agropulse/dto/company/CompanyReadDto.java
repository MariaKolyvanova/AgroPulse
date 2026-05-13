package ru.ochakovo.agropulse.dto;

public record CompanyReadDto (
        Long id,
        String name,
        String region,
        String industry
) {
}
