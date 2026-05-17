package ru.ochakovo.agropulse.service.report;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.ochakovo.agropulse.IntegrationTestBase;
import ru.ochakovo.agropulse.annotation.IT;

import static org.assertj.core.api.Assertions.assertThat;

@IT
class CalculatorServiceExpressionTest extends IntegrationTestBase {

    @Autowired
    CalculatorService calculatorService;

    @Test
    void evaluateWithExp4j_computesArithmeticExpression() {
        assertThat(calculatorService.evaluateWithExp4j("1.5 + 2.5")).isEqualTo(4.0);
        assertThat(calculatorService.evaluateWithExp4j("(1200.0 - 1500.0) / 100.0")).isEqualTo(-3.0);
    }
}
