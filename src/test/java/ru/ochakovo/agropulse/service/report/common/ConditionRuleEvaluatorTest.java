package ru.ochakovo.agropulse.service.report.common;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

class ConditionRuleEvaluatorTest {

    @ParameterizedTest
    @CsvSource({
            "value < 0.7, 0.69, true",
            "value < 0.7, 0.7, false",
            "value >= 0.7, 0.7, true",
            "value > 0.5, 0.51, true",
            "value <= 0.5, 0.5, true",
            "value > 0.8, 0.8, false"
    })
    void matches_singleComparison(String rule, double value, boolean expected) {
        assertThat(ConditionRuleEvaluator.matches(rule, value)).isEqualTo(expected);
    }

    @Test
    void matches_compoundRule() {
        assertThat(ConditionRuleEvaluator.matches("value >= 0.2 AND value <= 0.5", 0.35)).isTrue();
        assertThat(ConditionRuleEvaluator.matches("value >= 0.2 AND value <= 0.5", 0.15)).isFalse();
        assertThat(ConditionRuleEvaluator.matches("value > 0.05 AND value <= 0.1", 0.08)).isTrue();
    }

    @Test
    void matches_nullOrBlankRule_returnsFalse() {
        assertThat(ConditionRuleEvaluator.matches(null, 1.0)).isFalse();
        assertThat(ConditionRuleEvaluator.matches("  ", 1.0)).isFalse();
    }
}
