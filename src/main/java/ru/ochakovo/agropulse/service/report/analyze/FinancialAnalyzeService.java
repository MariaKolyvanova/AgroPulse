package ru.ochakovo.agropulse.service.report.analyze;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.ochakovo.agropulse.dto.report.analyze.*;
import ru.ochakovo.agropulse.entity.data.Report;
import ru.ochakovo.agropulse.entity.data.ValuesIndicatorsFinancialAnalyze;
import ru.ochakovo.agropulse.entity.dictionary.FinancialRatioCategory;
import ru.ochakovo.agropulse.entity.dictionary.IndicatorsFinancialAnalyze;
import ru.ochakovo.agropulse.repository.core.ReportRepository;
import ru.ochakovo.agropulse.repository.analyze.ValuesIndicatorsFinancialAnalyzeRepository;
import ru.ochakovo.agropulse.security.CustomUserDetails;
import ru.ochakovo.agropulse.service.report.common.AnalyzeReportSections;
import ru.ochakovo.agropulse.service.report.common.FinancialAnalyzeFormatter;
import ru.ochakovo.agropulse.service.report.core.ReportService;
import ru.ochakovo.agropulse.service.report.esg.EsgAnalyzeService;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class FinancialAnalyzeService {

    private final ReportRepository reportRepository;
    private final ValuesIndicatorsFinancialAnalyzeRepository valuesAnalyzeRepository;
    private final RecommendationService recommendationService;
    private final ReportService reportService;
    private final EsgAnalyzeService esgAnalyzeService;

    @Transactional(readOnly = true)
    public FinancialAnalyzePageDto buildAnalyzePage(Long reportId) {
        Report report = requireReport(reportId);
        List<ValuesIndicatorsFinancialAnalyze> values =
                valuesAnalyzeRepository.findByReportIdWithIndicators(reportId);

        Map<String, ValuesIndicatorsFinancialAnalyze> valuesByCode = values.stream()
                .collect(Collectors.toMap(
                        value -> value.getIndicatorsFinancialAnalyze().getCode(),
                        Function.identity(),
                        (left, right) -> left
                ));

        Map<String, IndicatorRecommendationPairDto> recommendationsByCode =
                recommendationService.findMatchingByIndicatorCode(values);

        List<String> periodLabels = List.of(
                String.valueOf(report.getReportYear() - 2),
                String.valueOf(report.getReportYear() - 1),
                String.valueOf(report.getReportYear())
        );

        List<FinancialAnalyzeSectionDto> sections = AnalyzeReportSections.SECTIONS.stream()
                .map(slot -> toSection(slot, periodLabels, valuesByCode, recommendationsByCode))
                .filter(section -> !section.indicators().isEmpty() || !section.subsections().isEmpty())
                .toList();

        List<FinancialAnalyzeSectionDto> esgSections =
                esgAnalyzeService.buildAnalyzeSections(report.getCompany().getId(), report.getReportYear());

        List<FinancialAnalyzeSummaryRowDto> summaryRows = buildSummaryRows(sections);
        appendEsgSummaryRows(summaryRows, esgSections);

        return new FinancialAnalyzePageDto(
                reportService.findComment(reportId),
                sections,
                esgSections,
                summaryRows
        );
    }

    private static void appendEsgSummaryRows(
            List<FinancialAnalyzeSummaryRowDto> rows,
            List<FinancialAnalyzeSectionDto> esgSections
    ) {
        for (FinancialAnalyzeSectionDto section : esgSections) {
            for (FinancialAnalyzeSectionDto subsection : section.subsections()) {
                appendSummaryRows(rows, section.title(), subsection.indicators());
            }
        }
    }

    private static List<FinancialAnalyzeSummaryRowDto> buildSummaryRows(List<FinancialAnalyzeSectionDto> sections) {
        List<FinancialAnalyzeSummaryRowDto> rows = new ArrayList<>();
        for (FinancialAnalyzeSectionDto section : sections) {
            appendSummaryRows(rows, section.title(), section.indicators());
            for (FinancialAnalyzeSectionDto subsection : section.subsections()) {
                appendSummaryRows(rows, section.title(), subsection.indicators());
            }
        }
        return rows;
    }

    private static void appendSummaryRows(
            List<FinancialAnalyzeSummaryRowDto> rows,
            String sectionTitle,
            List<FinancialAnalyzeIndicatorBlockDto> indicators
    ) {
        for (FinancialAnalyzeIndicatorBlockDto indicator : indicators) {
            rows.add(new FinancialAnalyzeSummaryRowDto(
                    sectionTitle,
                    indicator.indicatorName(),
                    indicator.unit(),
                    indicator.decimalPlaces(),
                    indicator.twoPeriodsAgoValue(),
                    indicator.previousPeriodValue(),
                    indicator.currentValue()
            ));
        }
    }

    @Transactional(readOnly = true)
    public Map<FinancialRatioCategory, FinancialAnalyzeCategoryTableDto> getTablesByCategory(Long reportId) {
        requireReport(reportId);

        List<ValuesIndicatorsFinancialAnalyze> values =
                valuesAnalyzeRepository.findByReportIdWithIndicators(reportId);

        Map<FinancialRatioCategory, List<FinancialAnalyzeRecommendationDto>> recommendationsByCategory =
                recommendationService.findMatchingByCategory(values);

        Map<FinancialRatioCategory, FinancialAnalyzeCategoryTableDto> tables =
                new EnumMap<>(FinancialRatioCategory.class);
        for (FinancialRatioCategory category : FinancialRatioCategory.values()) {
            FinancialAnalyzeCategoryTableDto table = toTable(category, values, recommendationsByCategory);
            if (table != null) {
                tables.put(category, table);
            }
        }
        return tables;
    }

    @Transactional(readOnly = true)
    public List<FinancialAnalyzeCategoryTableDto> getTablesForReport(Long reportId) {
        return Arrays.stream(FinancialRatioCategory.values())
                .map(getTablesByCategory(reportId)::get)
                .filter(Objects::nonNull)
                .toList();
    }

    private FinancialAnalyzeSectionDto toSection(
            AnalyzeReportSections.SectionSlot slot,
            List<String> periodLabels,
            Map<String, ValuesIndicatorsFinancialAnalyze> valuesByCode,
            Map<String, IndicatorRecommendationPairDto> recommendationsByCode
    ) {
        List<FinancialAnalyzeIndicatorBlockDto> indicators = new java.util.ArrayList<>();
        int indicatorIndex = 0;
        for (AnalyzeReportSections.IndicatorSlot indicatorSlot : slot.indicators()) {
            FinancialAnalyzeIndicatorBlockDto block = toIndicatorBlock(
                    indicatorSlot,
                    periodLabels,
                    valuesByCode.get(indicatorSlot.code()),
                    recommendationsByCode.get(indicatorSlot.code()),
                    indicatorIndex,
                    slot.chartType()
            );
            if (block != null) {
                indicators.add(block);
                indicatorIndex++;
            }
        }

        List<FinancialAnalyzeSectionDto> subsections = slot.subsections().stream()
                .map(sub -> toSection(sub, periodLabels, valuesByCode, recommendationsByCode))
                .filter(sub -> !sub.indicators().isEmpty())
                .toList();

        return new FinancialAnalyzeSectionDto(
                slot.title(),
                slot.chartType(),
                periodLabels,
                indicators,
                subsections,
                sectionShowsNormal(slot.title()),
                sectionUnitCaption(slot, valuesByCode)
        );
    }

    public static String sectionUnitCaption(
            AnalyzeReportSections.SectionSlot slot,
            Map<String, ValuesIndicatorsFinancialAnalyze> valuesByCode
    ) {
        if (slot.indicators().stream().allMatch(indicator -> AnalyzeReportSections.HIDES_UNIT_CAPTION.contains(indicator.code()))) {
            return null;
        }

        List<String> units = slot.indicators().stream()
                .map(indicatorSlot -> resolveDisplayUnit(indicatorSlot, valuesByCode.get(indicatorSlot.code())))
                .filter(Objects::nonNull)
                .filter(unit -> !unit.isBlank())
                .distinct()
                .toList();
        return FinancialAnalyzeFormatter.formatUnitCaption(units);
    }

    static String resolveDisplayUnit(
            AnalyzeReportSections.IndicatorSlot indicatorSlot,
            ValuesIndicatorsFinancialAnalyze value
    ) {
        if (AnalyzeReportSections.usesMillionsDisplay(indicatorSlot.displayScale())) {
            return AnalyzeReportSections.DISPLAY_UNIT_MILLIONS_RUB;
        }
        if (value == null) {
            return null;
        }
        return value.getIndicatorsFinancialAnalyze().getUnit();
    }

    private FinancialAnalyzeIndicatorBlockDto toIndicatorBlock(
            AnalyzeReportSections.IndicatorSlot slot,
            List<String> periodLabels,
            ValuesIndicatorsFinancialAnalyze value,
            IndicatorRecommendationPairDto recommendationPair,
            int indicatorIndex,
            ChartType chartType
    ) {
        if (value == null) {
            return null;
        }

        IndicatorsFinancialAnalyze indicator = value.getIndicatorsFinancialAnalyze();
        double scale = slot.displayScale();

        Double twoPeriodsAgo = scaleValue(value.getTwoPeriodsAgoValue(), scale);
        Double previous = scaleValue(value.getPreviousPeriodValue(), scale);
        Double current = scaleValue(value.getValue(), scale);

        String conclusion = recommendationPair != null ? recommendationPair.conclusion() : null;
        String recommendation = recommendationPair != null ? recommendationPair.recommendation() : null;

        return new FinancialAnalyzeIndicatorBlockDto(
                indicator.getCode(),
                indicator.getName(),
                AnalyzeReportSections.HIDES_UNIT_CAPTION.contains(indicator.getCode()) ? null : indicator.getUnit(),
                slot.seriesType(),
                slot.decimalPlaces(),
                twoPeriodsAgo,
                previous,
                current,
                FinancialAnalyzeFormatter.formatDelta(previous, current, periodLabels.get(1), periodLabels.get(2)),
                conclusion,
                recommendation,
                recommendationPair != null && recommendation != null && !recommendation.isBlank()
                        ? recommendationPair.tone()
                        : RecommendationTone.NEUTRAL,
                chartType == ChartType.LINE
                        ? ChartSeriesColors.forIndicator(indicatorIndex, chartType, slot.seriesType())
                        : null,
                FinancialAnalyzeFormatter.formatNormalValue(indicator.getNormalMin(), indicator.getNormalMax())
        );
    }

    public static boolean sectionShowsNormal(String sectionTitle) {
        return "Финансовая устойчивость".equals(sectionTitle)
                || "Коэффициенты ликвидности".equals(sectionTitle)
                || "Показатели рентабельности".equals(sectionTitle);
    }

    private static Double scaleValue(Double value, double scale) {
        if (value == null) {
            return null;
        }
        return value * scale;
    }

    private FinancialAnalyzeCategoryTableDto toTable(
            FinancialRatioCategory category,
            List<ValuesIndicatorsFinancialAnalyze> values,
            Map<FinancialRatioCategory, List<FinancialAnalyzeRecommendationDto>> recommendationsByCategory
    ) {
        List<FinancialAnalyzeIndicatorRowDto> rows = values.stream()
                .filter(value -> category.equals(value.getIndicatorsFinancialAnalyze().getCategory()))
                .map(this::toRow)
                .sorted(Comparator.comparing(FinancialAnalyzeIndicatorRowDto::indicatorName))
                .toList();

        if (rows.isEmpty()) {
            return null;
        }
        List<FinancialAnalyzeRecommendationDto> recommendations =
                recommendationsByCategory.getOrDefault(category, Collections.emptyList());
        return new FinancialAnalyzeCategoryTableDto(category.getLabel(), rows, recommendations);
    }

    private FinancialAnalyzeIndicatorRowDto toRow(ValuesIndicatorsFinancialAnalyze value) {
        IndicatorsFinancialAnalyze indicator = value.getIndicatorsFinancialAnalyze();
        return new FinancialAnalyzeIndicatorRowDto(
                indicator.getName(),
                value.getTwoPeriodsAgoValue(),
                value.getPreviousPeriodValue(),
                value.getValue(),
                FinancialAnalyzeFormatter.formatNormalValue(indicator.getNormalMin(), indicator.getNormalMax()),
                toDouble(indicator.getNormalMin()),
                toDouble(indicator.getNormalMax())
        );
    }

    private static Double toDouble(BigDecimal value) {
        return value == null ? null : value.doubleValue();
    }

    private Report requireReport(Long reportId) {
        Long companyId = CustomUserDetails.requireCurrent().getCompanyId();
        return reportRepository.findByIdAndCompanyId(reportId, companyId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }
}
