package ru.ochakovo.finpolicy.dto;

public record CreateCompanyDto(
        String companyName,
        String region,
        String industry
    ) {
}