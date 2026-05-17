package ru.ochakovo.agropulse.service.report;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.ochakovo.agropulse.entity.dictionary.IndicatorsFinancialAnalyze;
import ru.ochakovo.agropulse.repository.IndicatorsFinancialAnalyzeRepository;
import ru.ochakovo.agropulse.repository.IndicatorsFinancialReportsRepository;
import ru.ochakovo.agropulse.repository.ValuesIndicatorsFinancialAnalyzeRepository;
import ru.ochakovo.agropulse.repository.ValuesIndicatorsFinancialReportsRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class ValuesIndicatorsFinancialAnalyze {

    private final IndicatorsFinancialReportsRepository indicatorsReport;
    private final IndicatorsFinancialAnalyzeRepository indicatorAnalyze;
    private final ValuesIndicatorsFinancialAnalyzeRepository valuesAnalyze;
    private final ValuesIndicatorsFinancialReportsRepository valuesReports;

    private static final Pattern REPORT_CODE = Pattern.compile("\\b(\\d{4})\\b");

// Задача: 1. Выполнить финансовый анализ (посчитать вск финансовые показатели)
    /*
    Пройти по всем показателям анализа, получая его значение, если показателей не хватило - пропустить.
     */

    // с values_indicators_financial_reports

//     Записать значения в values_indicators_financial_analyze
    public void calculate(Long reportId) {
        List<IndicatorsFinancialAnalyze> indicatorsFinancialAnalyzes = indicatorAnalyze.findAll();

        indicatorsFinancialAnalyzes.stream().forEach(indicator -> {
            Double value = getValueByCode(reportId, indicator);
            valuesAnalyze.save()
        });
    }

    private Double getValueByCode(Long reportId, IndicatorsFinancialAnalyze indicator){
        String formula = indicator.getFormula();

        Object[] codes = REPORT_CODE.matcher(formula)
                                        .results()
                                        .map(m -> m.group(1))
                                        .toArray();

        List<Object> values = new ArrayList<>();

        Arrays.stream(codes).forEach(code -> {
            values.add(valuesReports.getValueByCode(reportId, code.toString()));
        });

        String formulaWithValue = String.format(formula, values.toArray());
        Double value = Double.valueOf(formulaWithValue);

        return value;
    }
}
