package ru.ochakovo.agropulse.dto.report.analyze;

public final class ChartSeriesColors {

    private static final String[] PALETTE = {
            "#37A2EB",
            "#FF6485",
            "#4CC0C0",
            "#FFA040",
            "#9A66FF",
            "#FFCD56",
            "#CACBCF"
    };

    private static final String LINE_COLOR = "#FFCD56";

    private ChartSeriesColors() {
    }

    public static String forIndicator(int index, ChartType chartType, ChartSeriesType seriesType) {
        if (chartType == ChartType.MIXED && seriesType == ChartSeriesType.LINE) {
            return LINE_COLOR;
        }
        return PALETTE[Math.floorMod(index, PALETTE.length)];
    }
}
