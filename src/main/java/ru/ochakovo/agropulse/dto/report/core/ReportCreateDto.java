package ru.ochakovo.agropulse.dto.report.core;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import ru.ochakovo.agropulse.entity.data.Month;

public record ReportCreateDto (
        @NotNull(message = "Укажите год")
        @Min(value = 2000, message = "Год не может быть меньше 2000")
        @Max(value = 2100, message = "Год не может быть больше 2100")
        Short reportYear,

        Month reportMonth,

        @NotBlank(message = "Введите название отчёта")
        @Size(max = 255, message = "Название не должно превышать 255 символов")
        String reportName
) {
}
