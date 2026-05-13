package ru.ochakovo.agropulse.dto.report;

import ru.ochakovo.agropulse.entity.data.ReportStatus;

public record ReportUpdateDto (
        ReportStatus status
){
}
