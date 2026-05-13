package ru.ochakovo.agropulse.dto.report;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import ru.ochakovo.agropulse.entity.data.Month;

public record ReportCreateDto (
        @NotNull
        Short reportYear,

        @NotNull
        Month reportMonth,

        @NotBlank
        String reportName
) {
}
