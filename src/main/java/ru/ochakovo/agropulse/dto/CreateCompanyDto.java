package ru.ochakovo.agropulse.dto;

public record CreateCompanyDto(
        String companyName,
        String region,
        String industry
    ) {
}