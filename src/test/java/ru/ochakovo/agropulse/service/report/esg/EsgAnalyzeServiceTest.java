package ru.ochakovo.agropulse.service.report.esg;

import org.junit.jupiter.api.Test;
import ru.ochakovo.agropulse.dto.report.analyze.RecommendationTone;
import ru.ochakovo.agropulse.entity.dictionary.EsgDirection;

import static org.assertj.core.api.Assertions.assertThat;

class EsgAnalyzeServiceTest {

    @Test
    void assessYearOverYearTone_lowerDirectionImprovementIsPositive() {
        assertThat(EsgAnalyzeService.assessYearOverYearTone(100.0, 80.0, EsgDirection.LOWER))
                .isEqualTo(RecommendationTone.POSITIVE);
    }

    @Test
    void assessYearOverYearTone_lowerDirectionWorseningIsWarning() {
        assertThat(EsgAnalyzeService.assessYearOverYearTone(80.0, 100.0, EsgDirection.LOWER))
                .isEqualTo(RecommendationTone.WARNING);
    }

    @Test
    void assessYearOverYearTone_higherDirectionImprovementIsPositive() {
        assertThat(EsgAnalyzeService.assessYearOverYearTone(0.70, 0.78, EsgDirection.HIGHER))
                .isEqualTo(RecommendationTone.POSITIVE);
    }

    @Test
    void assessYearOverYearTone_returnsNeutralWhenValuesMissing() {
        assertThat(EsgAnalyzeService.assessYearOverYearTone(null, 10.0, EsgDirection.LOWER))
                .isEqualTo(RecommendationTone.NEUTRAL);
    }
}
