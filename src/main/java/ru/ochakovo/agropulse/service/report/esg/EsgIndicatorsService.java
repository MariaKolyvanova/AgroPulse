package ru.ochakovo.agropulse.service.report.esg;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.ochakovo.agropulse.dto.report.esg.EsgCategorySectionDto;
import ru.ochakovo.agropulse.dto.report.esg.EsgIndicatorRowDto;
import ru.ochakovo.agropulse.dto.report.esg.EsgPageDto;
import ru.ochakovo.agropulse.entity.data.Report;
import ru.ochakovo.agropulse.entity.data.ValuesIndicatorsEsg;
import ru.ochakovo.agropulse.entity.dictionary.EsgCategory;
import ru.ochakovo.agropulse.entity.dictionary.IndicatorsEsg;
import ru.ochakovo.agropulse.entity.organization.Company;
import ru.ochakovo.agropulse.repository.core.ReportRepository;
import ru.ochakovo.agropulse.repository.esg.IndicatorsEsgRepository;
import ru.ochakovo.agropulse.repository.esg.ValuesIndicatorsEsgRepository;
import ru.ochakovo.agropulse.security.CustomUserDetails;

import java.time.Year;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class EsgIndicatorsService {

    private static final int MIN_YEAR = 2000;
    private static final int MAX_YEAR = 2100;

    private final IndicatorsEsgRepository indicatorsEsgRepository;
    private final ValuesIndicatorsEsgRepository valuesIndicatorsEsgRepository;
    private final ReportRepository reportRepository;

    @Transactional(readOnly = true)
    public EsgPageDto getPage(Integer year) {
        Long companyId = CustomUserDetails.requireCurrent().getCompanyId();
        int selectedYear = normalizeYear(year);
        return new EsgPageDto(selectedYear, buildSections(companyId, selectedYear));
    }

    @Transactional(readOnly = true)
    public List<Integer> getAvailableYears(Integer selectedYear) {
        int currentYear = Year.now().getValue();
        int startYear = 2019;
        List<Integer> years = new ArrayList<>();
        for (int year = currentYear; year >= startYear; year--) {
            years.add(year);
        }

        int normalizedYear = normalizeYear(selectedYear);
        if (!years.contains(normalizedYear)) {
            years.add(normalizedYear);
            years.sort(Comparator.reverseOrder());
        }

        return years;
    }

    @Transactional(readOnly = true)
    public List<EsgCategorySectionDto> getSectionsForReportYear(Long companyId, short reportYear) {
        return buildSections(companyId, reportYear);
    }

    @Transactional(readOnly = true)
    public List<EsgCategorySectionDto> getSectionsForReport(Long reportId) {
        Report report = requireReport(reportId);
        return buildSections(report.getCompany().getId(), report.getReportYear());
    }

    @Transactional
    public void saveValues(Long reportId, List<Long> indicatorIds, List<String> rawValues) {
        Report report = requireReport(reportId);
        saveValues(report.getReportYear(), indicatorIds, rawValues);
    }

    @Transactional
    public void saveValues(int year, List<Long> indicatorIds, List<String> rawValues) {
        Long companyId = CustomUserDetails.requireCurrent().getCompanyId();
        short reportYear = (short) normalizeYear(year);
        Company company = new Company();
        company.setId(companyId);

        valuesIndicatorsEsgRepository.deleteAllByCompany_IdAndReportYear(companyId, reportYear);

        if (indicatorIds == null || indicatorIds.isEmpty()) {
            return;
        }

        List<String> values = rawValues != null ? rawValues : List.of();
        List<ValuesIndicatorsEsg> toSave = new ArrayList<>();

        for (int i = 0; i < indicatorIds.size(); i++) {
            Long indicatorId = indicatorIds.get(i);
            String rawValue = i < values.size() ? values.get(i) : null;
            if (isBlank(rawValue)) {
                continue;
            }

            IndicatorsEsg indicator = indicatorsEsgRepository.findById(indicatorId)
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.BAD_REQUEST,
                            "Показатель ESG не найден: " + indicatorId
                    ));

            toSave.add(ValuesIndicatorsEsg.builder()
                    .company(company)
                    .reportYear(reportYear)
                    .indicatorsEsg(indicator)
                    .value(parseValue(indicator, rawValue))
                    .build());
        }

        if (toSave.isEmpty()) {
            return;
        }

        valuesIndicatorsEsgRepository.saveAll(toSave);
    }

    private Report requireReport(Long reportId) {
        Long companyId = CustomUserDetails.requireCurrent().getCompanyId();
        return reportRepository.findByIdAndCompanyId(reportId, companyId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    private List<EsgCategorySectionDto> buildSections(Long companyId, int selectedYear) {
        Map<Long, ValuesIndicatorsEsg> valuesByIndicatorId =
                valuesIndicatorsEsgRepository.findByCompanyIdAndReportYearWithIndicators(
                                companyId,
                                (short) selectedYear
                        ).stream()
                        .collect(Collectors.toMap(
                                value -> value.getIndicatorsEsg().getId(),
                                value -> value
                        ));

        Map<EsgCategory, List<IndicatorsEsg>> indicatorsByCategory = indicatorsEsgRepository.findAll().stream()
                .collect(Collectors.groupingBy(IndicatorsEsg::getCategory));

        return Arrays.stream(EsgCategory.values())
                .map(category -> toSection(category, indicatorsByCategory.getOrDefault(category, List.of()), valuesByIndicatorId))
                .filter(section -> !section.rows().isEmpty())
                .toList();
    }

    private EsgCategorySectionDto toSection(
            EsgCategory category,
            List<IndicatorsEsg> indicators,
            Map<Long, ValuesIndicatorsEsg> valuesByIndicatorId
    ) {
        List<EsgIndicatorRowDto> rows = indicators.stream()
                .sorted(Comparator.comparing(IndicatorsEsg::getIndicatorName))
                .map(indicator -> {
                    ValuesIndicatorsEsg stored = valuesByIndicatorId.get(indicator.getId());
                    return new EsgIndicatorRowDto(
                            indicator.getId(),
                            indicator.getIndicatorName(),
                            stored != null ? stored.getValue() : null
                    );
                })
                .toList();
        return new EsgCategorySectionDto(category.getLabel(), rows);
    }

    private int normalizeYear(Integer year) {
        int selectedYear = year != null ? year : Year.now().getValue();
        if (selectedYear < MIN_YEAR || selectedYear > MAX_YEAR) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Некорректный отчётный год");
        }
        return selectedYear;
    }

    private Double parseValue(IndicatorsEsg indicator, String rawValue) {
        try {
            double parsedValue = Double.parseDouble(rawValue.trim().replace(',', '.'));
            if (parsedValue < 0) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Значение «" + indicator.getIndicatorName() + "» не может быть отрицательным"
                );
            }
            return parsedValue;
        } catch (NumberFormatException ex) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Некорректное значение для показателя «" + indicator.getIndicatorName() + "»"
            );
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
