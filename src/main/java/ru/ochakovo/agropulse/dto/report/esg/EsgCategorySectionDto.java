package ru.ochakovo.agropulse.dto.report.esg;

import java.util.List;

public record EsgCategorySectionDto(
        String categoryName,
        List<EsgIndicatorRowDto> rows
) {
}
