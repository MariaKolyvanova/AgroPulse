package ru.ochakovo.agropulse.service.report.analyze;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ochakovo.agropulse.dto.report.analyze.FinancialAnalyzeRecommendationDto;
import ru.ochakovo.agropulse.dto.report.analyze.IndicatorRecommendationPairDto;
import ru.ochakovo.agropulse.dto.report.analyze.RecommendationTone;
import ru.ochakovo.agropulse.entity.data.ValuesIndicatorsFinancialAnalyze;
import ru.ochakovo.agropulse.entity.dictionary.FinancialRatioCategory;
import ru.ochakovo.agropulse.entity.dictionary.IndicatorsFinancialAnalyze;
import ru.ochakovo.agropulse.entity.dictionary.Recommendation;
import ru.ochakovo.agropulse.repository.analyze.RecommendationRepository;
import ru.ochakovo.agropulse.service.report.common.ConditionRuleEvaluator;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class RecommendationService {

    private static final Comparator<FinancialAnalyzeRecommendationDto> RECOMMENDATION_ORDER =
            Comparator.comparing(FinancialAnalyzeRecommendationDto::indicatorName)
                    .thenComparing(dto -> "Вывод".equals(dto.type()) ? 0 : 1);

    private static final Set<String> LOWER_IS_BETTER_CODES = Set.of(
            "DEBT_CAPITAL",
            "SHORT_TERM_LIABILITIES",
            "COST_OF_SALES",
            "ACCOUNTS_RECEIVABLE",
            "ACCOUNTS_PAYABLE",
            "P1_MOST_URGENT_LIABILITIES",
            "P2_SHORT_TERM_LIABILITIES",
            "P3_LONG_TERM_LIABILITIES",
            "EXTERNAL_LIABILITIES",
            "RECEIVABLES_COLLECTION_PERIOD",
            "INVENTORY_TURNOVER_DAYS"
    );

    private final RecommendationRepository recommendationRepository;

    @Transactional(readOnly = true)
    public Map<FinancialRatioCategory, List<FinancialAnalyzeRecommendationDto>> findMatchingByCategory(
            List<ValuesIndicatorsFinancialAnalyze> values
    ) {
        Map<FinancialRatioCategory, List<FinancialAnalyzeRecommendationDto>> result = new EnumMap<>(FinancialRatioCategory.class);
        if (values.isEmpty()) {
            return result;
        }

        Map<Long, ValuesIndicatorsFinancialAnalyze> valueByIndicatorId = values.stream()
                .filter(value -> value.getValue() != null)
                .collect(Collectors.toMap(
                        value -> value.getIndicatorsFinancialAnalyze().getId(),
                        value -> value,
                        (left, right) -> left
                ));

        if (valueByIndicatorId.isEmpty()) {
            return result;
        }

        Set<Long> indicatorIds = valueByIndicatorId.keySet();
        List<Recommendation> recommendations =
                recommendationRepository.findByIndicatorIdsWithIndicator(indicatorIds);

        for (Recommendation recommendation : recommendations) {
            IndicatorsFinancialAnalyze indicator = recommendation.getIndicatorsFinancialAnalyze();
            ValuesIndicatorsFinancialAnalyze indicatorValue = valueByIndicatorId.get(indicator.getId());
            if (indicatorValue == null) {
                continue;
            }
            if (!matchesRecommendation(recommendation, indicatorValue, indicator)) {
                continue;
            }

            FinancialRatioCategory category = indicator.getCategory();
            if (category == null) {
                continue;
            }

            result.computeIfAbsent(category, ignored -> new ArrayList<>())
                    .addAll(toDtos(indicator, recommendation));
        }

        result.replaceAll((category, recommendationsForCategory) ->
                recommendationsForCategory.stream()
                        .sorted(RECOMMENDATION_ORDER)
                        .toList()
        );

        return result;
    }

    @Transactional(readOnly = true)
    public Map<String, IndicatorRecommendationPairDto> findMatchingByIndicatorCode(
            List<ValuesIndicatorsFinancialAnalyze> values
    ) {
        Map<String, IndicatorRecommendationPairDto> result = new HashMap<>();
        if (values.isEmpty()) {
            return result;
        }

        Map<Long, ValuesIndicatorsFinancialAnalyze> valueByIndicatorId = values.stream()
                .filter(value -> value.getValue() != null)
                .collect(Collectors.toMap(
                        value -> value.getIndicatorsFinancialAnalyze().getId(),
                        value -> value,
                        (left, right) -> left
                ));

        if (valueByIndicatorId.isEmpty()) {
            return result;
        }

        List<Recommendation> recommendations =
                recommendationRepository.findByIndicatorIdsWithIndicator(valueByIndicatorId.keySet());

        Map<String, String> conclusions = new HashMap<>();
        Map<String, String> recommendationTexts = new HashMap<>();
        Map<String, String> conditionByCode = new HashMap<>();

        for (Recommendation recommendation : recommendations) {
            IndicatorsFinancialAnalyze indicator = recommendation.getIndicatorsFinancialAnalyze();
            ValuesIndicatorsFinancialAnalyze indicatorValue = valueByIndicatorId.get(indicator.getId());
            if (indicatorValue == null) {
                continue;
            }
            if (!matchesRecommendation(recommendation, indicatorValue, indicator)) {
                continue;
            }

            String code = indicator.getCode();
            if (hasText(recommendation.getConclusionText())) {
                conclusions.putIfAbsent(code, recommendation.getConclusionText());
            }
            if (hasText(recommendation.getRecommendationText())) {
                recommendationTexts.putIfAbsent(code, recommendation.getRecommendationText());
                conditionByCode.putIfAbsent(code, recommendation.getCondition());
            }
        }

        for (String code : conclusions.keySet()) {
            result.put(code, new IndicatorRecommendationPairDto(
                    conclusions.get(code),
                    recommendationTexts.get(code),
                    RecommendationTone.fromCondition(conditionByCode.get(code))
            ));
        }
        for (String code : recommendationTexts.keySet()) {
            result.putIfAbsent(code, new IndicatorRecommendationPairDto(
                    null,
                    recommendationTexts.get(code),
                    RecommendationTone.fromCondition(conditionByCode.get(code))
            ));
        }

        return result;
    }

    private static List<FinancialAnalyzeRecommendationDto> toDtos(
            IndicatorsFinancialAnalyze indicator,
            Recommendation recommendation
    ) {
        List<FinancialAnalyzeRecommendationDto> dtos = new ArrayList<>();
        if (hasText(recommendation.getConclusionText())) {
            dtos.add(new FinancialAnalyzeRecommendationDto(
                    indicator.getName(),
                    "Вывод",
                    recommendation.getConclusionText()
            ));
        }
        if (hasText(recommendation.getRecommendationText())) {
            dtos.add(new FinancialAnalyzeRecommendationDto(
                    indicator.getName(),
                    "Рекомендация",
                    recommendation.getRecommendationText()
            ));
        }
        return dtos;
    }

    private static boolean matchesRecommendation(
            Recommendation recommendation,
            ValuesIndicatorsFinancialAnalyze indicatorValue,
            IndicatorsFinancialAnalyze indicator
    ) {
        if (hasText(recommendation.getCondition())) {
            return recommendation.getCondition().equals(assessCondition(indicatorValue, indicator));
        }
        return ConditionRuleEvaluator.matches(
                recommendation.getConditionRule(),
                indicatorValue.getValue(),
                computeDiffPercent(indicatorValue),
                indicator.getNormalMin(),
                indicator.getNormalMax()
        );
    }

    private static String assessCondition(
            ValuesIndicatorsFinancialAnalyze indicatorValue,
            IndicatorsFinancialAnalyze indicator
    ) {
        Double value = indicatorValue.getValue();
        if (value == null) {
            return "normal";
        }

        BigDecimal normalMin = indicator.getNormalMin();
        BigDecimal normalMax = indicator.getNormalMax();
        boolean hasNormal = normalMin != null || normalMax != null;

        if (hasNormal && violatesNormal(value, normalMin, normalMax)) {
            return "critical";
        }

        Double diffPercent = computeDiffPercent(indicatorValue);
        if (diffPercent == null) {
            return "normal";
        }

        if (trendWorsens(indicator.getCode(), value, diffPercent, normalMin, normalMax)) {
            return hasNormal ? "warning" : "critical";
        }
        return "normal";
    }

    private static boolean violatesNormal(Double value, BigDecimal normalMin, BigDecimal normalMax) {
        return normalMin != null && value < normalMin.doubleValue()
                || normalMax != null && value > normalMax.doubleValue();
    }

    private static boolean trendWorsens(
            String code,
            Double value,
            Double diffPercent,
            BigDecimal normalMin,
            BigDecimal normalMax
    ) {
        if (diffPercent == 0) {
            return false;
        }
        if (normalMin != null && normalMax != null) {
            double midpoint = (normalMin.doubleValue() + normalMax.doubleValue()) / 2.0;
            return value <= midpoint ? diffPercent < 0 : diffPercent > 0;
        }
        if (normalMax != null && normalMin == null) {
            return diffPercent > 0;
        }
        if (normalMin != null) {
            return diffPercent < 0;
        }
        return LOWER_IS_BETTER_CODES.contains(code) ? diffPercent > 0 : diffPercent < 0;
    }

    private static Double computeDiffPercent(ValuesIndicatorsFinancialAnalyze indicatorValue) {
        Double previous = indicatorValue.getPreviousPeriodValue();
        Double current = indicatorValue.getValue();
        if (previous == null || current == null || previous == 0) {
            return null;
        }
        return ((current - previous) / Math.abs(previous)) * 100.0;
    }

    private static boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}
