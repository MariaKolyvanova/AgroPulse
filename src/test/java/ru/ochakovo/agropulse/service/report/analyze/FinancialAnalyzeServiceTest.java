package ru.ochakovo.agropulse.service.report.analyze;

import org.junit.jupiter.api.Test;
import ru.ochakovo.agropulse.dto.report.analyze.ChartSeriesColors;
import ru.ochakovo.agropulse.dto.report.analyze.ChartSeriesType;
import ru.ochakovo.agropulse.dto.report.analyze.ChartType;
import ru.ochakovo.agropulse.dto.report.analyze.RecommendationTone;
import ru.ochakovo.agropulse.service.report.common.AnalyzeReportSections;
import ru.ochakovo.agropulse.service.report.common.FinancialAnalyzeFormatter;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class FinancialAnalyzeServiceTest {

    @Test
    void formatNormalValue_formatsRangeMinMaxAndBounds() {
        assertThat(FinancialAnalyzeFormatter.formatNormalValue(null, null)).isNull();
        assertThat(FinancialAnalyzeFormatter.formatNormalValue(new BigDecimal("0.50"), new BigDecimal("1.00")))
                .isEqualTo("0.5 – 1");
        assertThat(FinancialAnalyzeFormatter.formatNormalValue(new BigDecimal("1.00"), null))
                .isEqualTo("≥ 1");
        assertThat(FinancialAnalyzeFormatter.formatNormalValue(null, new BigDecimal("0.70")))
                .isEqualTo("≤ 0.7");
    }

    @Test
    void formatUnitCaption_prefixesSingleSharedUnit() {
        assertThat(FinancialAnalyzeFormatter.formatUnitCaption(List.of("млн руб.")))
                .isEqualTo("в млн руб.");
        assertThat(FinancialAnalyzeFormatter.formatUnitCaption(List.of()))
                .isNull();
        assertThat(FinancialAnalyzeFormatter.formatUnitCaption(List.of("раз", "дни")))
                .isNull();
    }

    @Test
    void resolveDisplayUnit_usesMillionsForScaledBalanceIndicators() {
        var slot = new AnalyzeReportSections.IndicatorSlot(
                "DEBT_CAPITAL",
                ChartSeriesType.LINE,
                AnalyzeReportSections.THOUSANDS_TO_MILLIONS
        );

        assertThat(FinancialAnalyzeService.resolveDisplayUnit(slot, null))
                .isEqualTo("млн руб.");
    }

    @Test
    void sectionUnitCaption_showsMillionsForBalanceSectionWithMixedDbUnits() {
        var slot = new AnalyzeReportSections.SectionSlot(
                "Балансовые показатели",
                ChartType.LINE,
                List.of(
                        new AnalyzeReportSections.IndicatorSlot(
                                "EQUITY_CAPITAL",
                                ChartSeriesType.LINE,
                                AnalyzeReportSections.THOUSANDS_TO_MILLIONS
                        ),
                        new AnalyzeReportSections.IndicatorSlot(
                                "DEBT_CAPITAL",
                                ChartSeriesType.LINE,
                                AnalyzeReportSections.THOUSANDS_TO_MILLIONS
                        )
                )
        );

        assertThat(FinancialAnalyzeService.sectionUnitCaption(slot, Map.of()))
                .isEqualTo("в млн руб.");
    }

    @Test
    void sectionShowsNormal_onlyForCoefficientAndProfitabilitySections() {
        assertThat(FinancialAnalyzeService.sectionShowsNormal("Финансовая устойчивость")).isTrue();
        assertThat(FinancialAnalyzeService.sectionShowsNormal("Коэффициенты ликвидности")).isTrue();
        assertThat(FinancialAnalyzeService.sectionShowsNormal("Показатели рентабельности")).isTrue();
        assertThat(FinancialAnalyzeService.sectionShowsNormal("Балансовые показатели")).isFalse();
    }

    @Test
    void chartSeriesColors_matchesPrintChartPalette() {
        assertThat(ChartSeriesColors.forIndicator(0, ChartType.BAR, ChartSeriesType.BAR))
                .isEqualTo("#37A2EB");
        assertThat(ChartSeriesColors.forIndicator(1, ChartType.LINE, ChartSeriesType.LINE))
                .isEqualTo("#FF6485");
        assertThat(ChartSeriesColors.forIndicator(2, ChartType.BAR, ChartSeriesType.BAR))
                .isEqualTo("#4CC0C0");
    }

    @Test
    void formatChangePercent_formatsRussianStyle() {
        assertThat(FinancialAnalyzeFormatter.formatChangePercent(3.42, 3.19))
                .isEqualTo("(-6,72%)");
        assertThat(FinancialAnalyzeFormatter.formatChangePercent(3.19, 3.15))
                .isEqualTo("(-1,25%)");
        assertThat(FinancialAnalyzeFormatter.formatChangePercent(3.0, 3.3))
                .isEqualTo("(+9,99%)");
        assertThat(FinancialAnalyzeFormatter.formatChangePercent(null, 3.15)).isNull();
    }

    @Test
    void formatDelta_formatsPercentChange() {
        assertThat(FinancialAnalyzeFormatter.formatDelta(100.0, 105.3, "2023", "2024"))
                .isEqualTo("Δ 2024/2023: +5.3%");
        assertThat(FinancialAnalyzeFormatter.formatDelta(100.0, 90.0, "2023", "2024"))
                .isEqualTo("Δ 2024/2023: -10%");
        assertThat(FinancialAnalyzeFormatter.formatDelta(null, 90.0, "2023", "2024")).isNull();
    }

    @Test
    void fromPositive_mapsDatabaseFlagToTone() {
        assertThat(RecommendationTone.fromPositive(true)).isEqualTo(RecommendationTone.POSITIVE);
        assertThat(RecommendationTone.fromPositive(false)).isEqualTo(RecommendationTone.WARNING);
        assertThat(RecommendationTone.fromPositive(null)).isEqualTo(RecommendationTone.NEUTRAL);
    }
}
