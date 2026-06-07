package ru.ochakovo.agropulse.service.report.analyze;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.ochakovo.agropulse.dto.report.analyze.FinancialAnalyzeRecommendationDto;
import ru.ochakovo.agropulse.dto.report.analyze.IndicatorRecommendationPairDto;
import ru.ochakovo.agropulse.dto.report.analyze.RecommendationTone;
import ru.ochakovo.agropulse.entity.data.ValuesIndicatorsFinancialAnalyze;
import ru.ochakovo.agropulse.entity.dictionary.FinancialRatioCategory;
import ru.ochakovo.agropulse.entity.dictionary.IndicatorsFinancialAnalyze;
import ru.ochakovo.agropulse.entity.dictionary.Recommendation;
import ru.ochakovo.agropulse.repository.analyze.RecommendationRepository;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RecommendationServiceTest {

    @Mock
    RecommendationRepository recommendationRepository;

    @InjectMocks
    RecommendationService recommendationService;

    @Test
    void findMatchingByCategory_returnsMatchingRecommendationsGroupedByCategory() {
        IndicatorsFinancialAnalyze indicator = IndicatorsFinancialAnalyze.builder()
                .name("Коэффициент соотношения заемных и собственных средств")
                .code("DEBT_TO_EQUITY_RATIO")
                .category(FinancialRatioCategory.FINANCIAL_STABILITY)
                .build();
        indicator.setId(26L);

        ValuesIndicatorsFinancialAnalyze value = ValuesIndicatorsFinancialAnalyze.builder()
                .indicatorsFinancialAnalyze(indicator)
                .value(0.8)
                .build();

        Recommendation rule = Recommendation.builder()
                .conditionRule("value >= 0.7")
                .conclusionText("Предприятие сильно зависит от заёмных средств, финансовая устойчивость снижена.")
                .recommendationText("оптимизировать структуру капитала.")
                .condition("")
                .indicatorsFinancialAnalyze(indicator)
                .build();

        when(recommendationRepository.findByIndicatorIdsWithIndicator(anyCollection()))
                .thenReturn(List.of(rule));

        Map<FinancialRatioCategory, List<FinancialAnalyzeRecommendationDto>> result =
                recommendationService.findMatchingByCategory(List.of(value));

        assertThat(result).containsKey(FinancialRatioCategory.FINANCIAL_STABILITY);
        assertThat(result.get(FinancialRatioCategory.FINANCIAL_STABILITY))
                .hasSize(2)
                .anySatisfy(dto -> {
                    assertThat(dto.type()).isEqualTo("Вывод");
                    assertThat(dto.description()).contains("заёмных средств");
                })
                .anySatisfy(dto -> {
                    assertThat(dto.type()).isEqualTo("Рекомендация");
                    assertThat(dto.description()).contains("структуру капитала");
                });
    }

    @Test
    void findMatchingByIndicatorCode_pairsConclusionAndRecommendationFromSingleRule() {
        IndicatorsFinancialAnalyze indicator = IndicatorsFinancialAnalyze.builder()
                .name("Коэффициент текущей ликвидности")
                .code("CURRENT_LIQUIDITY_RATIO")
                .category(FinancialRatioCategory.LIQUIDITY_COEFFICIENTS)
                .build();
        indicator.setId(22L);

        ValuesIndicatorsFinancialAnalyze value = ValuesIndicatorsFinancialAnalyze.builder()
                .indicatorsFinancialAnalyze(indicator)
                .value(1.2)
                .build();

        Recommendation rule = Recommendation.builder()
                .conditionRule("value < 1.5")
                .conclusionText("Риск неплатёжеспособности: оборотных активов недостаточно.")
                .recommendationText("увеличивать оборотные активы или сокращать краткосрочные кредиты.")
                .condition("")
                .indicatorsFinancialAnalyze(indicator)
                .build();

        when(recommendationRepository.findByIndicatorIdsWithIndicator(anyCollection()))
                .thenReturn(List.of(rule));

        Map<String, IndicatorRecommendationPairDto> result =
                recommendationService.findMatchingByIndicatorCode(List.of(value));

        assertThat(result).containsKey("CURRENT_LIQUIDITY_RATIO");
        assertThat(result.get("CURRENT_LIQUIDITY_RATIO").conclusion()).contains("недостаточно");
        assertThat(result.get("CURRENT_LIQUIDITY_RATIO").recommendation()).contains("оборотные активы");
        assertThat(result.get("CURRENT_LIQUIDITY_RATIO").tone()).isEqualTo(RecommendationTone.NEUTRAL);
    }

    @Test
    void findMatchingByCategory_skipsWhenCurrentValueIsNull() {
        IndicatorsFinancialAnalyze indicator = IndicatorsFinancialAnalyze.builder()
                .name("Коэффициент автономии")
                .category(FinancialRatioCategory.FINANCIAL_STABILITY)
                .build();
        indicator.setId(26L);

        ValuesIndicatorsFinancialAnalyze value = ValuesIndicatorsFinancialAnalyze.builder()
                .indicatorsFinancialAnalyze(indicator)
                .value(null)
                .build();

        Map<FinancialRatioCategory, List<FinancialAnalyzeRecommendationDto>> result =
                recommendationService.findMatchingByCategory(List.of(value));

        assertThat(result).isEmpty();
    }
}
