package ru.ochakovo.agropulse.dto.report.esg;

import java.util.List;

public record EsgPageDto(
        int selectedYear,
        List<EsgCategorySectionDto> sections
) {
}
