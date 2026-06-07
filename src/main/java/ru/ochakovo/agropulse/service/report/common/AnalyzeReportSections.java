package ru.ochakovo.agropulse.service.report.common;

import ru.ochakovo.agropulse.dto.report.analyze.ChartSeriesType;
import ru.ochakovo.agropulse.dto.report.analyze.ChartType;

import java.util.List;
import java.util.Set;

public final class AnalyzeReportSections {

    public static final double THOUSANDS_TO_MILLIONS = 0.001;
    public static final String DISPLAY_UNIT_MILLIONS_RUB = "млн руб.";

    public static boolean usesMillionsDisplay(double displayScale) {
        return Math.abs(displayScale - THOUSANDS_TO_MILLIONS) < 1e-12;
    }

    public record IndicatorSlot(
            String code,
            ChartSeriesType seriesType,
            double displayScale,
            int decimalPlaces,
            boolean showNormal
    ) {
        public IndicatorSlot(String code, ChartSeriesType seriesType) {
            this(code, seriesType, 1.0, 0, false);
        }

        public IndicatorSlot(String code, ChartSeriesType seriesType, double displayScale) {
            this(code, seriesType, displayScale, 0, false);
        }
    }

    public record SectionSlot(
            String title,
            ChartType chartType,
            List<IndicatorSlot> indicators,
            List<SectionSlot> subsections
    ) {
        public SectionSlot(String title, ChartType chartType, List<IndicatorSlot> indicators) {
            this(title, chartType, indicators, List.of());
        }
    }

    public static final List<SectionSlot> SECTIONS = List.of(
            new SectionSlot(
                    "Основные экономические показатели",
                    ChartType.LINE,
                    List.of(
                            indicator("REVENUE", ChartSeriesType.LINE, THOUSANDS_TO_MILLIONS),
                            indicator("GROSS_PROFIT", ChartSeriesType.LINE, THOUSANDS_TO_MILLIONS),
                            indicator("COST_OF_SALES", ChartSeriesType.LINE, THOUSANDS_TO_MILLIONS),
                            indicator("OPERATING_PROFIT", ChartSeriesType.LINE, THOUSANDS_TO_MILLIONS),
                            indicator("NET_PROFIT", ChartSeriesType.LINE, THOUSANDS_TO_MILLIONS)
                    )
            ),
            new SectionSlot(
                    "Балансовые показатели",
                    ChartType.LINE,
                    List.of(
                            indicator("EQUITY_CAPITAL", ChartSeriesType.LINE, THOUSANDS_TO_MILLIONS),
                            indicator("DEBT_CAPITAL", ChartSeriesType.LINE, THOUSANDS_TO_MILLIONS),
                            indicator("P3_LONG_TERM_LIABILITIES", ChartSeriesType.LINE, THOUSANDS_TO_MILLIONS),
                            indicator("SHORT_TERM_LIABILITIES", ChartSeriesType.LINE, THOUSANDS_TO_MILLIONS),
                            indicator("NON_CURRENT_ASSETS", ChartSeriesType.LINE, THOUSANDS_TO_MILLIONS),
                            indicator("CURRENT_ASSETS", ChartSeriesType.LINE, THOUSANDS_TO_MILLIONS)
                    )
            ),
            new SectionSlot(
                    "Финансовая устойчивость",
                    ChartType.BAR,
                    List.of(
                            coefficient("DEBT_TO_EQUITY_RATIO"),
                            coefficient("AUTONOMY_RATIO"),
                            coefficient("MANEUVERABILITY_RATIO"),
                            coefficient("MOBILE_TO_IMMOBILE_RATIO"),
                            coefficient("OWN_WORKING_CAPITAL_RATIO")
                    )
            ),
            new SectionSlot(
                    "Коэффициенты ликвидности",
                    ChartType.BAR,
                    List.of(
                            coefficient("CURRENT_LIQUIDITY_RATIO"),
                            coefficient("ABSOLUTE_LIQUIDITY_RATIO"),
                            coefficient("QUICK_LIQUIDITY_RATIO")
                    )
            ),
            new SectionSlot(
                    "Запасы",
                    ChartType.BAR,
                    List.of(),
                    List.of(
                            new SectionSlot(
                                    "Коэффициент оборачиваемости запасов",
                                    ChartType.BAR,
                                    List.of(barIndicator("INVENTORY_TURNOVER_RATIO", 2))
                            ),
                            new SectionSlot(
                                    "Продолжительность одного оборота запасов (в днях)",
                                    ChartType.BAR,
                                    List.of(barIndicator("INVENTORY_TURNOVER_DAYS"))
                            )
                    )
            ),
            new SectionSlot(
                    "Дебиторская задолженность",
                    ChartType.BAR,
                    List.of(),
                    List.of(
                            new SectionSlot(
                                    "Оборачиваемость дебиторской задолженности",
                                    ChartType.BAR,
                                    List.of(barIndicator("RECEIVABLES_TURNOVER", 2))
                            ),
                            new SectionSlot(
                                    "Период погашения дебиторской задолженности (в днях)",
                                    ChartType.BAR,
                                    List.of(barIndicator("RECEIVABLES_COLLECTION_PERIOD"))
                            )
                    )
            ),
            new SectionSlot(
                    "Показатели рентабельности",
                    ChartType.BAR,
                    List.of(
                            profitability("PRODUCTION_PROFITABILITY_RATIO"),
                            profitability("SALES_PROFITABILITY_ROS"),
                            profitability("RETURN_ON_ASSETS_ROA"),
                            profitability("RETURN_ON_EQUITY_ROE")
                    )
            )
    );

    public static final Set<String> HIDES_UNIT_CAPTION = Set.of(
            "RECEIVABLES_TURNOVER",
            "RECEIVABLES_COLLECTION_PERIOD",
            "INVENTORY_TURNOVER_RATIO",
            "INVENTORY_TURNOVER_DAYS"
                                                        );

    private static IndicatorSlot indicator(String code, ChartSeriesType seriesType) {
        return new IndicatorSlot(code, seriesType);
    }

    private static IndicatorSlot indicator(String code, ChartSeriesType seriesType, double displayScale) {
        return new IndicatorSlot(code, seriesType, displayScale);
    }

    private static IndicatorSlot coefficient(String code) {
        return new IndicatorSlot(code, ChartSeriesType.BAR, 1.0, 2, true);
    }

    private static IndicatorSlot profitability(String code) {
        return new IndicatorSlot(code, ChartSeriesType.BAR, 1.0, 2, true);
    }

    private static IndicatorSlot barIndicator(String code) {
        return new IndicatorSlot(code, ChartSeriesType.BAR);
    }

    private static IndicatorSlot barIndicator(String code, int decimalPlaces) {
        return new IndicatorSlot(code, ChartSeriesType.BAR, 1.0, decimalPlaces, false);
    }

    private AnalyzeReportSections() {
    }
}
