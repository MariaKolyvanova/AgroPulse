package ru.ochakovo.agropulse.dto.company;

public record CompanyReadDto (
        Long id,
        String name,
        String region,
        String industry
) {
}
