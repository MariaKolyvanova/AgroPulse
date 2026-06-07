package ru.ochakovo.agropulse.dto.report.analyze;

import java.util.Arrays;
import java.util.List;

public record FinancialAnalyzePageDto(
        String analystComment,
        List<FinancialAnalyzeSectionDto> sections,
        List<FinancialAnalyzeSectionDto> esgSections,
        List<FinancialAnalyzeSummaryRowDto> summaryRows
) {
    public List<String> analystCommentParagraphs() {
        if (analystComment == null || analystComment.isBlank()) {
            return List.of();
        }
        String normalized = analystComment.strip();
        String[] blocks = normalized.split("\\R\\R+");
        if (blocks.length == 1 && normalized.contains("\n")) {
            return Arrays.stream(normalized.split("\\R"))
                    .map(String::trim)
                    .filter(s -> !s.isBlank())
                    .toList();
        }
        return Arrays.stream(blocks)
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .toList();
    }
}
