package ru.ochakovo.agropulse.service.report.core;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import ru.ochakovo.agropulse.entity.data.ReportStatus;

import static org.assertj.core.api.Assertions.assertThat;

class ReportWorkflowTest {

    @Test
    void xmlReplaced_allowsPrepareReplaceAndApproveForAnalysis() {
        assertThat(ReportWorkflow.canPrepare(ReportStatus.XML_REPLACED)).isTrue();
        assertThat(ReportWorkflow.canReplaceXml(ReportStatus.XML_REPLACED)).isTrue();
        assertThat(ReportWorkflow.canApproveForAnalysis(ReportStatus.XML_REPLACED)).isTrue();
    }

    @Test
    void uploaded_allowsPrepareAndApproveForAnalysis() {
        assertThat(ReportWorkflow.canPrepare(ReportStatus.UPLOADED)).isTrue();
        assertThat(ReportWorkflow.canApproveForAnalysis(ReportStatus.UPLOADED)).isTrue();
        assertThat(ReportWorkflow.canCalculate(ReportStatus.UPLOADED)).isFalse();
        assertThat(ReportWorkflow.canExportPdf(ReportStatus.UPLOADED)).isFalse();
    }

    @Test
    void approvedForAnalysis_allowsCalculateAndViewData() {
        assertThat(ReportWorkflow.canCalculate(ReportStatus.APPROVED_FOR_ANALYSIS)).isTrue();
        assertThat(ReportWorkflow.canPrepare(ReportStatus.APPROVED_FOR_ANALYSIS)).isTrue();
        assertThat(ReportWorkflow.canReplaceXml(ReportStatus.APPROVED_FOR_ANALYSIS)).isFalse();
        assertThat(ReportWorkflow.canApproveForAnalysis(ReportStatus.APPROVED_FOR_ANALYSIS)).isFalse();
    }

    @Test
    void calculationsCompleted_allowsAnalysis() {
        assertThat(ReportWorkflow.canOpenAnalysis(ReportStatus.CALCULATIONS_COMPLETED)).isTrue();
        assertThat(ReportWorkflow.canSaveAnalystComment(ReportStatus.CALCULATIONS_COMPLETED)).isTrue();
        assertThat(ReportWorkflow.canApproveReport(ReportStatus.CALCULATIONS_COMPLETED)).isFalse();
    }

    @Test
    void commentsAdded_allowsApproveReport() {
        assertThat(ReportWorkflow.canApproveReport(ReportStatus.COMMENTS_ADDED)).isTrue();
        assertThat(ReportWorkflow.canOpenAnalysis(ReportStatus.COMMENTS_ADDED)).isTrue();
    }

    @Test
    void approved_allowsAnalysisExportAndCommentEdit() {
        assertThat(ReportWorkflow.canOpenAnalysis(ReportStatus.APPROVED)).isTrue();
        assertThat(ReportWorkflow.canExportPdf(ReportStatus.APPROVED)).isTrue();
        assertThat(ReportWorkflow.canSaveAnalystComment(ReportStatus.APPROVED)).isTrue();
        assertThat(ReportWorkflow.canApproveReport(ReportStatus.APPROVED)).isFalse();
    }

    @ParameterizedTest
    @EnumSource(ReportStatus.class)
    void exportOnlyWhenApproved(ReportStatus status) {
        if (status == ReportStatus.APPROVED) {
            assertThat(ReportWorkflow.canExportPdf(status)).isTrue();
        } else {
            assertThat(ReportWorkflow.canExportPdf(status)).isFalse();
        }
    }
}
