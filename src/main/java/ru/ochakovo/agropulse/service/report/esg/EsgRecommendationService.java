package ru.ochakovo.agropulse.service.report.esg;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ochakovo.agropulse.dto.report.analyze.IndicatorRecommendationPairDto;
import ru.ochakovo.agropulse.dto.report.analyze.RecommendationTone;
import ru.ochakovo.agropulse.entity.data.ValuesIndicatorsEsg;
import ru.ochakovo.agropulse.entity.dictionary.IndicatorsEsg;
import ru.ochakovo.agropulse.entity.dictionary.RecommendationEsg;
import ru.ochakovo.agropulse.repository.esg.RecommendationEsgRepository;
import ru.ochakovo.agropulse.repository.esg.ValuesIndicatorsEsgRepository;
import ru.ochakovo.agropulse.service.report.common.ConditionRuleEvaluator;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class EsgRecommendationService {

    private final RecommendationEsgRepository recommendationEsgRepository;
    private final ValuesIndicatorsEsgRepository valuesIndicatorsEsgRepository;

    @Transactional(readOnly = true)
    public Map<String, IndicatorRecommendationPairDto> findMatchingByIndicatorCode(
            Long companyId,
            short previousYear,
            short currentYear
    ) {
        Map<Long, Double> previousValues = loadValuesByIndicatorId(companyId, previousYear);
        Map<Long, Double> currentValues = loadValuesByIndicatorId(companyId, currentYear);

        Set<Long> indicatorIds = new HashSet<>(previousValues.keySet());
        indicatorIds.addAll(currentValues.keySet());
        if (indicatorIds.isEmpty()) {
            return Map.of();
        }

        List<RecommendationEsg> recommendations =
                recommendationEsgRepository.findByIndicatorIdsWithIndicator(indicatorIds);

        Map<String, String> conclusions = new HashMap<>();
        Map<String, String> recommendationTexts = new HashMap<>();
        Map<String, Boolean> positiveByCode = new HashMap<>();

        for (RecommendationEsg recommendation : recommendations) {
            IndicatorsEsg indicator = recommendation.getIndicatorsEsg();
            Double changePercent = computeChangePercent(
                    previousValues.get(indicator.getId()),
                    currentValues.get(indicator.getId())
            );
            if (changePercent == null) {
                continue;
            }
            if (!ConditionRuleEvaluator.matches(recommendation.getConditionRule(), changePercent)) {
                continue;
            }

            String code = indicator.getCode();
            if (hasText(recommendation.getConclusionText())) {
                conclusions.putIfAbsent(code, recommendation.getConclusionText());
            }
            if (hasText(recommendation.getRecommendationText())) {
                recommendationTexts.putIfAbsent(code, recommendation.getRecommendationText());
                positiveByCode.putIfAbsent(code, recommendation.getPositive());
            }
        }

        Map<String, IndicatorRecommendationPairDto> result = new HashMap<>();
        for (String code : conclusions.keySet()) {
            result.put(code, new IndicatorRecommendationPairDto(
                    conclusions.get(code),
                    recommendationTexts.get(code),
                    RecommendationTone.fromPositive(positiveByCode.get(code))
            ));
        }
        for (String code : recommendationTexts.keySet()) {
            result.putIfAbsent(code, new IndicatorRecommendationPairDto(
                    null,
                    recommendationTexts.get(code),
                    RecommendationTone.fromPositive(positiveByCode.get(code))
            ));
        }
        return result;
    }

    static Double computeChangePercent(Double previous, Double current) {
        if (previous == null || current == null) {
            return null;
        }
        if (previous == 0) {
            if (current == 0) {
                return 0.0;
            }
            return current > 0 ? 100.0 : -100.0;
        }
        return ((current - previous) / Math.abs(previous)) * 100.0;
    }

    private Map<Long, Double> loadValuesByIndicatorId(Long companyId, short reportYear) {
        return valuesIndicatorsEsgRepository.findByCompanyIdAndReportYearWithIndicators(companyId, reportYear)
                .stream()
                .collect(Collectors.toMap(
                        value -> value.getIndicatorsEsg().getId(),
                        ValuesIndicatorsEsg::getValue,
                        (left, right) -> left
                ));
    }

    private static boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}
