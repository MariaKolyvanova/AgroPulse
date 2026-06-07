package ru.ochakovo.agropulse.service.report.common;

import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class ConditionRuleEvaluator {

    private static final Pattern COMPARISON = Pattern.compile(
            "(value|diff_percent)\\s*(<=|>=|==|=|<|>)\\s*([+-]?[\\d.]+|normal_min|normal_max)",
            Pattern.CASE_INSENSITIVE
    );

    private ConditionRuleEvaluator() {
    }

    public static boolean matches(String conditionRule, double value) {
        return matches(conditionRule, value, null, null, null);
    }

    public static boolean matches(String conditionRule, double value, BigDecimal normalMin, BigDecimal normalMax) {
        return matches(conditionRule, value, null, normalMin, normalMax);
    }

    public static boolean matches(
            String conditionRule,
            double value,
            Double diffPercent,
            BigDecimal normalMin,
            BigDecimal normalMax
    ) {
        if (conditionRule == null || conditionRule.isBlank()) {
            return false;
        }
        String[] parts = conditionRule.split("(?i)\\s+AND\\s+");
        for (String part : parts) {
            if (!matchesPart(part.trim(), value, diffPercent, normalMin, normalMax)) {
                return false;
            }
        }
        return true;
    }

    private static boolean matchesPart(
            String part,
            double value,
            Double diffPercent,
            BigDecimal normalMin,
            BigDecimal normalMax
    ) {
        Matcher matcher = COMPARISON.matcher(part);
        if (!matcher.matches()) {
            return false;
        }
        Double actual = resolveActual(matcher.group(1), value, diffPercent);
        Double threshold = resolveThreshold(matcher.group(3), normalMin, normalMax);
        if (actual == null) {
            return false;
        }
        if (threshold == null) {
            return false;
        }
        return switch (matcher.group(2)) {
            case "<" -> actual < threshold;
            case "<=" -> actual <= threshold;
            case ">" -> actual > threshold;
            case ">=" -> actual >= threshold;
            case "=", "==" -> Double.compare(actual, threshold) == 0;
            default -> false;
        };
    }

    private static Double resolveActual(String operand, double value, Double diffPercent) {
        return switch (operand.toLowerCase()) {
            case "value" -> value;
            case "diff_percent" -> diffPercent;
            default -> null;
        };
    }

    private static Double resolveThreshold(String rawThreshold, BigDecimal normalMin, BigDecimal normalMax) {
        return switch (rawThreshold.toLowerCase()) {
            case "normal_min" -> normalMin != null ? normalMin.doubleValue() : null;
            case "normal_max" -> normalMax != null ? normalMax.doubleValue() : null;
            default -> Double.parseDouble(rawThreshold);
        };
    }
}
