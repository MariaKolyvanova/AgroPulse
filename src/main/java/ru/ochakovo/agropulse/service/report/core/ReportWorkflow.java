package ru.ochakovo.agropulse.service.report.core;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import ru.ochakovo.agropulse.entity.data.ReportStatus;

/**
 * Последовательность этапов отчёта:
 * загрузка → (замена документа) → подготовка → утверждение к анализу →
 * расчёт → анализ (комментарий) → одобрение → экспорт PDF.
 * ESG-показатели вводятся отдельно и не влияют на статус отчёта.
 */
public final class ReportWorkflow {

    private ReportWorkflow() {
    }

    private static boolean isPrepareStage(ReportStatus status) {
        return status == ReportStatus.UPLOADED
                || status == ReportStatus.XML_REPLACED;
    }

    /** Шаг 1: просмотр данных отчёта (редактирование — только на этапе подготовки). */
    public static boolean canPrepare(ReportStatus status) {
        return isPrepareStage(status) || status == ReportStatus.APPROVED_FOR_ANALYSIS;
    }

    /** Замена XML-файла и показателей отчётности на этапе подготовки данных. */
    public static boolean canReplaceXml(ReportStatus status) {
        return isPrepareStage(status);
    }

    /** Шаг 2: утверждение к анализу. */
    public static boolean canApproveForAnalysis(ReportStatus status) {
        return isPrepareStage(status);
    }

    /** Шаг 3: расчёт финансовых показателей. */
    public static boolean canCalculate(ReportStatus status) {
        return status == ReportStatus.APPROVED_FOR_ANALYSIS;
    }

    /** Шаг 4: просмотр страницы анализа (включая одобренный отчёт). */
    public static boolean canOpenAnalysis(ReportStatus status) {
        return status == ReportStatus.CALCULATIONS_COMPLETED
                || status == ReportStatus.COMMENTS_ADDED
                || status == ReportStatus.APPROVED;
    }

    /** Шаг 4: сохранение комментария аналитика (включая одобренный отчёт). */
    public static boolean canSaveAnalystComment(ReportStatus status) {
        return canOpenAnalysis(status);
    }

    /** Шаг 5: финальное одобрение отчёта. */
    public static boolean canApproveReport(ReportStatus status) {
        return status == ReportStatus.COMMENTS_ADDED;
    }

    /** Шаг 6: формирование PDF. */
    public static boolean canExportPdf(ReportStatus status) {
        return status == ReportStatus.APPROVED;
    }

    public static void require(boolean allowed) {
        if (!allowed) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Действие недоступно на текущем этапе отчёта"
            );
        }
    }

    public static void requirePrepare(ReportStatus status) {
        require(canPrepare(status));
    }

    public static void requireReplaceXml(ReportStatus status) {
        require(canReplaceXml(status));
    }

    public static void requireApproveForAnalysis(ReportStatus status) {
        require(canApproveForAnalysis(status));
    }

    public static void requireCalculate(ReportStatus status) {
        require(canCalculate(status));
    }

    public static void requireOpenAnalysis(ReportStatus status) {
        require(canOpenAnalysis(status));
    }

    public static void requireSaveAnalystComment(ReportStatus status) {
        require(canSaveAnalystComment(status));
    }

    public static void requireApproveReport(ReportStatus status) {
        require(canApproveReport(status));
    }

    public static void requireExportPdf(ReportStatus status) {
        require(canExportPdf(status));
    }
}
