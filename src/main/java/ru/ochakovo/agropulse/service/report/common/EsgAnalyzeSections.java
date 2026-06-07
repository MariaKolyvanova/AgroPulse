package ru.ochakovo.agropulse.service.report.common;

import ru.ochakovo.agropulse.dto.report.analyze.ChartType;
import ru.ochakovo.agropulse.entity.dictionary.EsgCategory;
import ru.ochakovo.agropulse.entity.dictionary.EsgDirection;

import java.util.List;
import java.util.Map;

public final class EsgAnalyzeSections {

    public record IndicatorSlot(String code, int decimalPlaces, EsgDirection direction) {
    }

    public record CategorySlot(EsgCategory category, List<IndicatorSlot> indicators) {
        public String title() {
            return category.getLabel();
        }
    }

    public static final ChartType CHART_TYPE = ChartType.LINE;

    public static final List<CategorySlot> CATEGORIES = List.of(
            new CategorySlot(
                    EsgCategory.ENVIRONMENTAL,
                    List.of(
                            slot("AIR_EMISSIONS", 0, EsgDirection.LOWER),
                            slot("GHG_EMISSIONS", 0, EsgDirection.LOWER),
                            slot("ENERGY_EFFICIENCY", 0, EsgDirection.LOWER),
                            slot("WATER_USE", 0, EsgDirection.LOWER),
                            slot("WASTE_MANAGEMENT", 0, EsgDirection.LOWER),
                            slot("RESPONSIBLE_FINANCING", 2, EsgDirection.HIGHER)
                    )
            ),
            new CategorySlot(
                    EsgCategory.SOCIAL,
                    List.of(
                            slot("LABOR_PRODUCTIVITY", 1, EsgDirection.HIGHER),
                            slot("OCCUPATIONAL_SAFETY", 1, EsgDirection.HIGHER),
                            slot("PAYROLL_SOCIAL_PROGRAMS", 0, EsgDirection.HIGHER),
                            slot("STAFF_TURNOVER", 1, EsgDirection.LOWER),
                            slot("STAFF_TRAINING", 1, EsgDirection.HIGHER),
                            slot("SOCIAL_INVESTMENTS", 0, EsgDirection.HIGHER)
                    )
            ),
            new CategorySlot(
                    EsgCategory.GOVERNANCE,
                    List.of(
                            slot("CYBERSECURITY", 1, EsgDirection.HIGHER),
                            slot("LEADERSHIP_ENGAGEMENT", 1, EsgDirection.HIGHER),
                            slot("RISK_MANAGEMENT", 1, EsgDirection.HIGHER),
                            slot("SUSTAINABILITY_FOCUS", 1, EsgDirection.HIGHER)
                    )
            )
    );

    private static final Map<String, IndicatorSlot> SLOTS_BY_CODE = CATEGORIES.stream()
            .flatMap(category -> category.indicators().stream())
            .collect(java.util.stream.Collectors.toMap(
                    IndicatorSlot::code,
                    slot -> slot,
                    (left, right) -> left
            ));

    static int decimalPlaces(String code) {
        IndicatorSlot slot = SLOTS_BY_CODE.get(code);
        return slot != null ? slot.decimalPlaces() : 0;
    }

    static EsgDirection direction(String code) {
        IndicatorSlot slot = SLOTS_BY_CODE.get(code);
        return slot != null ? slot.direction() : null;
    }

    private static IndicatorSlot slot(String code, int decimalPlaces, EsgDirection direction) {
        return new IndicatorSlot(code, decimalPlaces, direction);
    }

    private EsgAnalyzeSections() {
    }
}
