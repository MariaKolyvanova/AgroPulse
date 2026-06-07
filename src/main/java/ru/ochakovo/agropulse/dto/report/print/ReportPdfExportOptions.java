package ru.ochakovo.agropulse.dto.report.print;

public record ReportPdfExportOptions(
        boolean esg,
        boolean tables,
        boolean charts,
        boolean recommendations,
        boolean comment
) {
    public static ReportPdfExportOptions allEnabled() {
        return new ReportPdfExportOptions(true, true, true, true, true);
    }
}
