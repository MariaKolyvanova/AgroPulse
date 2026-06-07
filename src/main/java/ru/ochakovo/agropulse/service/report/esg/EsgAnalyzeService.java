package ru.ochakovo.agropulse.service.report.esg;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ochakovo.agropulse.dto.report.analyze.*;
import ru.ochakovo.agropulse.entity.dictionary.EsgDirection;
import ru.ochakovo.agropulse.entity.dictionary.IndicatorsEsg;
import ru.ochakovo.agropulse.repository.esg.IndicatorsEsgRepository;
import ru.ochakovo.agropulse.repository.esg.ValuesIndicatorsEsgRepository;
import ru.ochakovo.agropulse.service.report.common.EsgAnalyzeSections;
import ru.ochakovo.agropulse.service.report.common.FinancialAnalyzeFormatter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequiredArgsConstructor
@Service
public class EsgAnalyzeService {

    private final IndicatorsEsgRepository indicatorsEsgRepository;
    private final ValuesIndicatorsEsgRepository valuesIndicatorsEsgRepository;
    private final EsgRecommendationService esgRecommendationService;

    @Transactional(readOnly = true)
    public List<FinancialAnalyzeSectionDto> buildAnalyzeSections(Long companyId, short reportYear) {
        short previousYear = (short) (reportYear - 1);
        Map<String, IndicatorsEsg> indicatorsByCode = indicatorsEsgRepository.findAll().stream()
                .collect(Collectors.toMap(IndicatorsEsg::getCode, Function.identity(), (left, right) -> left));
        Map<String, Double> previousValues = loadValuesByCode(companyId, previousYear);
        Map<String, Double> currentValues = loadValuesByCode(companyId, reportYear);
        Map<String, IndicatorRecommendationPairDto> recommendationsByCode =
                esgRecommendationService.findMatchingByIndicatorCode(companyId, previousYear, reportYear);

        List<String> periodLabels = List.of(
                String.valueOf(previousYear),
                String.valueOf(reportYear)
        );

        List<FinancialAnalyzeSectionDto> sections = new ArrayList<>();
        for (EsgAnalyzeSections.CategorySlot categorySlot : EsgAnalyzeSections.CATEGORIES) {
            List<FinancialAnalyzeSectionDto> subsections = new ArrayList<>();
            for (EsgAnalyzeSections.IndicatorSlot slot : categorySlot.indicators()) {
                FinancialAnalyzeIndicatorBlockDto block = toIndicatorBlock(
                        indicatorsByCode.get(slot.code()),
                        slot,
                        periodLabels,
                        previousValues.get(slot.code()),
                        currentValues.get(slot.code()),
                        recommendationsByCode.get(slot.code())
                );
                if (block == null) {
                    continue;
                }
                IndicatorsEsg indicator = indicatorsByCode.get(slot.code());
                subsections.add(new FinancialAnalyzeSectionDto(
                        indicator.getIndicatorName(),
                        EsgAnalyzeSections.CHART_TYPE,
                        periodLabels,
                        List.of(block),
                        List.of()
                ));
            }
            if (!subsections.isEmpty()) {
                sections.add(new FinancialAnalyzeSectionDto(
                        categorySlot.title(),
                        EsgAnalyzeSections.CHART_TYPE,
                        periodLabels,
                        List.of(),
                        subsections,
                        false,
                        null,
                        true
                ));
            }
        }
        return sections;
    }

    @Transactional(readOnly = true)
    public boolean hasAnalyzeData(List<FinancialAnalyzeSectionDto> sections) {
        if (sections == null || sections.isEmpty()) {
            return false;
        }
        return sections.stream()
                .flatMap(this::streamIndicators)
                .anyMatch(indicator -> indicator.previousPeriodValue() != null || indicator.currentValue() != null);
    }

    private Stream<FinancialAnalyzeIndicatorBlockDto> streamIndicators(FinancialAnalyzeSectionDto section) {
        if (!section.indicators().isEmpty()) {
            return section.indicators().stream();
        }
        return section.subsections().stream()
                .flatMap(subsection -> subsection.indicators().stream());
    }

    private Map<String, Double> loadValuesByCode(Long companyId, short reportYear) {
        return valuesIndicatorsEsgRepository.findByCompanyIdAndReportYearWithIndicators(companyId, reportYear)
                .stream()
                .collect(Collectors.toMap(
                        value -> value.getIndicatorsEsg().getCode(),
                        value -> value.getValue(),
                        (left, right) -> left
                ));
    }

    private FinancialAnalyzeIndicatorBlockDto toIndicatorBlock(
            IndicatorsEsg indicator,
            EsgAnalyzeSections.IndicatorSlot slot,
            List<String> periodLabels,
            Double previousValue,
            Double currentValue,
            IndicatorRecommendationPairDto recommendationPair
    ) {
        if (indicator == null || (previousValue == null && currentValue == null)) {
            return null;
        }

        String conclusion = recommendationPair != null ? recommendationPair.conclusion() : null;
        String recommendation = recommendationPair != null ? recommendationPair.recommendation() : null;
        RecommendationTone tone = recommendationPair != null && recommendation != null && !recommendation.isBlank()
                ? recommendationPair.tone()
                : assessYearOverYearTone(previousValue, currentValue, slot.direction());

        return new FinancialAnalyzeIndicatorBlockDto(
                indicator.getCode(),
                indicator.getIndicatorName(),
                null,
                ChartSeriesType.LINE,
                slot.decimalPlaces(),
                null,
                previousValue,
                currentValue,
                FinancialAnalyzeFormatter.formatDelta(
                        previousValue,
                        currentValue,
                        periodLabels.get(0),
                        periodLabels.get(1)
                ),
                conclusion,
                recommendation,
                tone,
                ChartSeriesColors.forIndicator(0, EsgAnalyzeSections.CHART_TYPE, ChartSeriesType.LINE),
                null
        );
    }

    static RecommendationTone assessYearOverYearTone(Double previous, Double current, EsgDirection direction) {
        if (previous == null || current == null || direction == null) {
            return RecommendationTone.NEUTRAL;
        }
        if (Double.compare(previous, current) == 0) {
            return RecommendationTone.NEUTRAL;
        }
        boolean increased = current > previous;
        boolean positive = direction == EsgDirection.HIGHER ? increased : !increased;
        return positive ? RecommendationTone.POSITIVE : RecommendationTone.WARNING;
    }
}
