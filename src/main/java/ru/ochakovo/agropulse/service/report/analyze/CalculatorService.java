package ru.ochakovo.agropulse.service.report.analyze;


import lombok.RequiredArgsConstructor;
import net.objecthunter.exp4j.ExpressionBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ochakovo.agropulse.dto.report.core.WarningDto;
import ru.ochakovo.agropulse.entity.data.ReportValuePeriod;
import ru.ochakovo.agropulse.entity.data.ValuesIndicatorsFinancialAnalyze;
import ru.ochakovo.agropulse.entity.dictionary.FinancialRatioCategory;
import ru.ochakovo.agropulse.entity.dictionary.IndicatorsFinancialAnalyze;
import ru.ochakovo.agropulse.entity.dictionary.IndicatorsFinancialReports;
import ru.ochakovo.agropulse.repository.analyze.IndicatorsFinancialAnalyzeRepository;
import ru.ochakovo.agropulse.repository.analyze.ValuesIndicatorsFinancialAnalyzeRepository;
import ru.ochakovo.agropulse.repository.core.ReportRepository;
import ru.ochakovo.agropulse.repository.xml.IndicatorsFinancialReportsRepository;
import ru.ochakovo.agropulse.repository.xml.ValuesIndicatorsFinancialReportsRepository;
import ru.ochakovo.agropulse.service.report.xml.FinancialReportIndicatorImportService;

import java.util.*;
import java.util.regex.Pattern;

@Transactional
@Service
@RequiredArgsConstructor
public class CalculatorService {

    private final IndicatorsFinancialReportsRepository indicatorsReport;
    private final IndicatorsFinancialAnalyzeRepository indicatorAnalyze;
    private final ValuesIndicatorsFinancialAnalyzeRepository valuesAnalyze;
    private final ValuesIndicatorsFinancialReportsRepository valuesReports;
    private final ReportRepository reportRepository;

    private static final Pattern REPORT_CODE = Pattern.compile("\\b(\\d{4})\\b");

    public void calculateIndicatorsFinancialAnalyze(Long reportId) {
        valuesAnalyze.deleteAllByReport_Id(reportId);
        var report = reportRepository.getReferenceById(reportId);
        List<IndicatorsFinancialAnalyze> indicatorsFinancialAnalyzes = indicatorAnalyze.findAll();

        indicatorsFinancialAnalyzes.forEach(indicator -> {
            Double value = asStoredValue(calculateIndicatorValue(reportId, indicator, ReportValuePeriod.CURRENT), indicator);
            if (value == null) {
                return;
            }
            valuesAnalyze.save(ValuesIndicatorsFinancialAnalyze.builder()
                    .report(report)
                    .indicatorsFinancialAnalyze(indicator)
                    .value(value)
                    .previousPeriodValue(asStoredValue(
                            calculateIndicatorValue(reportId, indicator, ReportValuePeriod.PREVIOUS), indicator))
                    .twoPeriodsAgoValue(asStoredValue(
                            calculateIndicatorValue(reportId, indicator, ReportValuePeriod.TWO_PERIODS_AGO), indicator))
                    .build());
        });
    }

    private static Double asStoredValue(Double value, IndicatorsFinancialAnalyze indicator) {
        if (value == null) {
            return null;
        }
        if (indicator.getCategory() == FinancialRatioCategory.PROFITABILITY) {
            return value * 100.0;
        }
        return value;
    }

    private Double calculateIndicatorValue(Long reportId, IndicatorsFinancialAnalyze indicator, ReportValuePeriod period) {
        String formula = indicator.getFormula();
        if (formula == null || formula.isBlank()) {
            return null;
        }

        Object[] codes = REPORT_CODE.matcher(formula)
                .results()
                .map(m -> m.group(1))
                .toArray();

        if (codes.length == 1) {
            return getReportValueByCode(reportId, codes[0].toString(), period);
        }

        formula = formula.replaceAll(REPORT_CODE.pattern(), "%f");

        List<Object> values = new ArrayList<>();

        for (Object code : codes) {
            Double reportValue = getReportValueByCode(reportId, code.toString(), period);
            if (reportValue == null) {
                return null;
            }
            values.add(reportValue);
        }

        return evaluateWithExp4j(String.format(Locale.US, formula, values.toArray()));
    }

    private Double getReportValueByCode(Long reportId, String code, ReportValuePeriod period) {
        Double value = switch (period) {
            case CURRENT -> valuesReports.getValueByCode(reportId, code);
            case PREVIOUS -> valuesReports.getPreviousPeriodValueByCode(reportId, code);
            case TWO_PERIODS_AGO -> valuesReports.getTwoPeriodsAgoValueByCode(reportId, code);
        };
        if (value != null) {
            return value;
        }
        if (FinancialReportIndicatorImportService.ZERO_WHEN_ABSENT.contains(code)) {
            return 0.0;
        }
        return null;
    }

    public double evaluateWithExp4j(String expression) {
        return new ExpressionBuilder(expression)
                .build()
                .evaluate();
    }

    @Transactional(readOnly = true)
    public WarningDto getWarning(Long reportId){
        List<IndicatorsFinancialReports> allIndicatorsReport = indicatorsReport.findAll();
        Set<String> presentReportCodes = new HashSet<>();
        valuesReports.findByReportId(reportId).forEach(v ->
                presentReportCodes.add(v.getIndicatorsFinancialReports().getCode()));

        List<IndicatorsFinancialReports> missingReportIndicators = new ArrayList<>();

        allIndicatorsReport.forEach(code -> {
            if (!presentReportCodes.contains(code.getCode()))
                missingReportIndicators.add(code);
        });

        Set<String> missingReportCodes = new HashSet<>();
        missingReportIndicators.forEach(report -> missingReportCodes.add(report.getCode()));

        Set<IndicatorsFinancialAnalyze> missingAnalyzeIndicators = new LinkedHashSet<>();

        indicatorAnalyze.findAll().forEach(analyze -> {
            String formula = analyze.getFormula();
            if (formula == null || formula.isBlank()) {
                return;
            }
            boolean cannotCalculate = REPORT_CODE.matcher(formula)
                    .results()
                    .map(match -> match.group(1))
                    .anyMatch(missingReportCodes::contains);
            if (cannotCalculate) {
                missingAnalyzeIndicators.add(analyze);
            }
        });

        return new WarningDto(
                missingAnalyzeIndicators.stream().map(IndicatorsFinancialAnalyze::getName).toList(),
                missingReportIndicators.stream().map(IndicatorsFinancialReports::getIndicatorName).toList()
        );
    }
}
