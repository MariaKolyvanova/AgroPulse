package ru.ochakovo.agropulse.entity.data;

public enum ReportStatus {
    UPLOADED("Загружен"),
    PROCESSING("В обработке"),
    VALIDATING("Проверяется"),
    FAILED("Ошибка"),
    EDITING("Редактируется"),
    VALIDATED("Проверен"),
    READY("Готов"),
    CALCULATION("Расчёт"),
    COMPLETED("Завершён"),
    REVIEW("На рассмотрении"),
    APPROVED("Одобрен");

    private final String label;

    ReportStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

}