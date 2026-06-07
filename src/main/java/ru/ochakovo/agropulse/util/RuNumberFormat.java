package ru.ochakovo.agropulse.util;

import org.springframework.stereotype.Component;
import ru.ochakovo.agropulse.service.report.common.FinancialAnalyzeFormatter;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

@Component("ruNumbers")
public class RuNumberFormat {

    private static final DecimalFormat INTEGER_FORMAT;
    private static final DecimalFormat DECIMAL_FORMAT;

    static {
        DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance(Locale.forLanguageTag("ru-RU"));
        symbols.setGroupingSeparator(' ');
        symbols.setDecimalSeparator(',');
        INTEGER_FORMAT = new DecimalFormat("#,##0", symbols);
        INTEGER_FORMAT.setGroupingUsed(true);
        DECIMAL_FORMAT = new DecimalFormat("#,##0.00", symbols);
        DECIMAL_FORMAT.setGroupingUsed(true);
    }

    public String format(Number value) {
        return format(value, 0);
    }

    public String format(Number value, int decimalPlaces) {
        if (value == null) {
            return null;
        }
        if (decimalPlaces <= 0) {
            return INTEGER_FORMAT.format(Math.round(value.doubleValue()));
        }
        return DECIMAL_FORMAT.format(value.doubleValue());
    }

    public String formatDecimal(Number value) {
        return format(value, 2);
    }

    public String changePercent(Double previous, Double current) {
        return FinancialAnalyzeFormatter.formatChangePercent(previous, current);
    }
}
