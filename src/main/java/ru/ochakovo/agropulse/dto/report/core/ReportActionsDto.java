package ru.ochakovo.agropulse.dto.report.core;

import ru.ochakovo.agropulse.entity.data.ReportStatus;
import ru.ochakovo.agropulse.service.report.core.ReportWorkflow;

public record ReportActionsDto(
        boolean prepare,
        boolean replaceXml,
        boolean approveForAnalysis,
        boolean calculate,
        boolean analysis,
        boolean approve,
        boolean exportPdf
) {

    public static ReportActionsDto from(ReportStatus status) {
        return new ReportActionsDto(
                ReportWorkflow.canPrepare(status),
                ReportWorkflow.canReplaceXml(status),
                ReportWorkflow.canApproveForAnalysis(status),
                ReportWorkflow.canCalculate(status),
                ReportWorkflow.canOpenAnalysis(status),
                ReportWorkflow.canApproveReport(status),
                ReportWorkflow.canExportPdf(status)
        );
    }
}
