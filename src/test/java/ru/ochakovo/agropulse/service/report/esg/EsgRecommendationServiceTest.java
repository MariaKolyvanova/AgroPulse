package ru.ochakovo.agropulse.service.report.esg;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.ochakovo.agropulse.dto.report.analyze.IndicatorRecommendationPairDto;
import ru.ochakovo.agropulse.dto.report.analyze.RecommendationTone;
import ru.ochakovo.agropulse.entity.data.ValuesIndicatorsEsg;
import ru.ochakovo.agropulse.entity.dictionary.IndicatorsEsg;
import ru.ochakovo.agropulse.entity.dictionary.RecommendationEsg;
import ru.ochakovo.agropulse.repository.esg.RecommendationEsgRepository;
import ru.ochakovo.agropulse.repository.esg.ValuesIndicatorsEsgRepository;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EsgRecommendationServiceTest {

    @Mock
    RecommendationEsgRepository recommendationEsgRepository;

    @Mock
    ValuesIndicatorsEsgRepository valuesIndicatorsEsgRepository;

    @InjectMocks
    EsgRecommendationService esgRecommendationService;

    @Test
    void computeChangePercent_calculatesYearOverYearDifference() {
        assertThat(EsgRecommendationService.computeChangePercent(42150.0, 40800.0))
                .isCloseTo(-3.203, org.assertj.core.data.Offset.offset(0.01));
        assertThat(EsgRecommendationService.computeChangePercent(72500.0, 81200.0))
                .isCloseTo(12.0, org.assertj.core.data.Offset.offset(0.01));
    }

    @Test
    void computeChangePercent_handlesZeroPreviousValue() {
        assertThat(EsgRecommendationService.computeChangePercent(0.0, 120.0)).isEqualTo(100.0);
        assertThat(EsgRecommendationService.computeChangePercent(0.0, 0.0)).isEqualTo(0.0);
    }

    @Test
    void findMatchingByIndicatorCode_usesPercentChangeInRules() {
        IndicatorsEsg indicator = IndicatorsEsg.builder()
                .code("GHG_EMISSIONS")
                .indicatorName("Выбросы парниковых газов, т CO₂-экв.")
                .build();
        indicator.setId(1L);

        when(valuesIndicatorsEsgRepository.findByCompanyIdAndReportYearWithIndicators(1L, (short) 2025))
                .thenReturn(List.of(value(indicator, 42150.0)));
        when(valuesIndicatorsEsgRepository.findByCompanyIdAndReportYearWithIndicators(1L, (short) 2026))
                .thenReturn(List.of(value(indicator, 40800.0)));

        RecommendationEsg rule = RecommendationEsg.builder()
                .conditionRule("value <= 0")
                .conclusionText("Выбросы CO₂ снизились или остались на прежнем уровне по сравнению с прошлым годом.")
                .recommendationText("поддерживать программы энергоэффективности.")
                .positive(true)
                .indicatorsEsg(indicator)
                .build();

        when(recommendationEsgRepository.findByIndicatorIdsWithIndicator(anyCollection()))
                .thenReturn(List.of(rule));

        Map<String, IndicatorRecommendationPairDto> result =
                esgRecommendationService.findMatchingByIndicatorCode(1L, (short) 2025, (short) 2026);

        assertThat(result).containsKey("GHG_EMISSIONS");
        assertThat(result.get("GHG_EMISSIONS").conclusion()).contains("снизились");
        assertThat(result.get("GHG_EMISSIONS").recommendation()).contains("энергоэффективности");
        assertThat(result.get("GHG_EMISSIONS").tone()).isEqualTo(RecommendationTone.POSITIVE);
    }

    private static ValuesIndicatorsEsg value(IndicatorsEsg indicator, double amount) {
        return ValuesIndicatorsEsg.builder()
                .indicatorsEsg(indicator)
                .value(amount)
                .build();
    }
}
