package ru.ochakovo.agropulse.service.report.common;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Locale;

public final class FinancialAnalyzeFormatter {

    private static final DecimalFormat PERCENT_COMMA_FORMAT;
    private static final DecimalFormat DELTA_PERCENT_FORMAT;

    static {
        DecimalFormatSymbols commaSymbols = DecimalFormatSymbols.getInstance(Locale.forLanguageTag("ru-RU"));
        commaSymbols.setDecimalSeparator(',');
        PERCENT_COMMA_FORMAT = new DecimalFormat("0.00", commaSymbols);
        PERCENT_COMMA_FORMAT.setRoundingMode(RoundingMode.DOWN);

        DecimalFormatSymbols dotSymbols = DecimalFormatSymbols.getInstance(Locale.ROOT);
        dotSymbols.setDecimalSeparator('.');
        DELTA_PERCENT_FORMAT = new DecimalFormat("0.#", dotSymbols);
    }

    private FinancialAnalyzeFormatter() {
    }

    public static String formatNormalValue(BigDecimal min, BigDecimal max) {
        if (min == null && max == null) {
            return null;
        }
        if (min != null && max != null) {
            return formatCoefficient(min) + " – " + formatCoefficient(max);
        }
        if (min != null) {
            return "≥ " + formatCoefficient(min);
        }
        return "≤ " + formatCoefficient(max);
    }

    public static String formatUnitCaption(List<String> units) {
        if (units == null || units.isEmpty()) {
            return null;
        }
        String first = units.getFirst();
        if (first == null || first.isBlank()) {
            return null;
        }
        boolean allSame = units.stream().allMatch(first::equals);
        if (!allSame) {
            return null;
        }
        return "в " + first;
    }

    public static String formatChangePercent(Double previous, Double current) {
        if (previous == null || current == null || previous == 0.0) {
            return null;
        }
        double change = (current - previous) / previous * 100.0;
        String formatted = PERCENT_COMMA_FORMAT.format(Math.abs(change));
        if (change > 0) {
            return "(+" + formatted + "%)";
        }
        if (change < 0) {
            return "(-" + formatted + "%)";
        }
        return "(0,00%)";
    }

    public static String formatDelta(Double previous, Double current, String previousLabel, String currentLabel) {
        if (previous == null || current == null || previous == 0.0) {
            return null;
        }
        double change = (current - previous) / previous * 100.0;
        String sign = change >= 0 ? "+" : "-";
        String formatted = DELTA_PERCENT_FORMAT.format(Math.abs(change));
        return "Δ " + currentLabel + "/" + previousLabel + ": " + sign + formatted + "%";
    }

    private static String formatCoefficient(BigDecimal value) {
        return value.stripTrailingZeros().toPlainString();
    }
}
