package ru.ochakovo.agropulse.dto;

public record CompanyCreateDto(
        String companyName,
        String region,
        String industry
    ) {
}