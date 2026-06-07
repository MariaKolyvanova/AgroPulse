package ru.ochakovo.agropulse.dto.report.analyze;

import java.util.List;

public record FinancialAnalyzeSectionDto(
        String title,
        ChartType chartType,
        List<String> periodLabels,
        List<FinancialAnalyzeIndicatorBlockDto> indicators,
        List<FinancialAnalyzeSectionDto> subsections,
        String chartSvgUri,
        String secondaryChartSvgUri,
        boolean showNormal,
        String unitCaption,
        boolean esgSection
) {
    public FinancialAnalyzeSectionDto(
            String title,
            ChartType chartType,
            List<String> periodLabels,
            List<FinancialAnalyzeIndicatorBlockDto> indicators,
            List<FinancialAnalyzeSectionDto> subsections
    ) {
        this(title, chartType, periodLabels, indicators, subsections, null, null, false, null, false);
    }

    public FinancialAnalyzeSectionDto(
            String title,
            ChartType chartType,
            List<String> periodLabels,
            List<FinancialAnalyzeIndicatorBlockDto> indicators,
            List<FinancialAnalyzeSectionDto> subsections,
            boolean showNormal,
            String unitCaption
    ) {
        this(title, chartType, periodLabels, indicators, subsections, null, null, showNormal, unitCaption, false);
    }

    public FinancialAnalyzeSectionDto(
            String title,
            ChartType chartType,
            List<String> periodLabels,
            List<FinancialAnalyzeIndicatorBlockDto> indicators,
            List<FinancialAnalyzeSectionDto> subsections,
            boolean showNormal,
            String unitCaption,
            boolean esgSection
    ) {
        this(title, chartType, periodLabels, indicators, subsections, null, null, showNormal, unitCaption, esgSection);
    }
}
