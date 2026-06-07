package ru.ochakovo.agropulse.service.report.xml;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ochakovo.agropulse.dto.report.xml.FinancialXmlParsedRow;
import ru.ochakovo.agropulse.entity.data.Report;
import ru.ochakovo.agropulse.entity.data.ValuesIndicatorsFinancialReports;
import ru.ochakovo.agropulse.repository.xml.IndicatorsFinancialReportsRepository;
import ru.ochakovo.agropulse.repository.xml.ValuesIndicatorsFinancialReportsRepository;

import javax.xml.stream.XMLStreamException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FinancialReportIndicatorImportService {

    public static final Set<String> ZERO_WHEN_ABSENT = Set.of(
            "1215",
            "1220",
            "1230",
            "1240",
            "1250",
            "1260",
            "1430",
            "1530",
            "1540"
    );

    private final XmlParser xmlParser;
    private final IndicatorsFinancialReportsRepository indicatorsFinancialReportsRepository;
    private final ValuesIndicatorsFinancialReportsRepository valuesIndicatorsFinancialReportsRepository;

    @Transactional
    public List<ValuesIndicatorsFinancialReports> importFromXml(Report report, InputStream xml) throws XMLStreamException {
        List<FinancialXmlParsedRow> rows = xmlParser.parse(xml);
        Map<String, FinancialXmlParsedRow> parsedByCode = rows.stream()
                .collect(Collectors.toMap(FinancialXmlParsedRow::indicatorCode, Function.identity()));

        valuesIndicatorsFinancialReportsRepository.deleteAllByReport_Id(report.getId());

        List<ValuesIndicatorsFinancialReports> saved = new ArrayList<>();
        for (FinancialXmlParsedRow row : rows) {
            var indicator = indicatorsFinancialReportsRepository.findByCode(row.indicatorCode())
                    .orElseThrow(() -> new IllegalStateException("Нет показателя с кодом " + row.indicatorCode()));

            saved.add(valuesIndicatorsFinancialReportsRepository.save(
                    ValuesIndicatorsFinancialReports.builder()
                            .report(report)
                            .indicatorsFinancialReports(indicator)
                            .value(row.reportingValue())
                            .previousPeriodValue(row.previousPeriodValue())
                            .twoPeriodsAgoValue(row.twoPeriodsAgoValue())
                            .build()));
        }

        for (String code : ZERO_WHEN_ABSENT) {
            if (parsedByCode.containsKey(code)) {
                continue;
            }
            var indicator = indicatorsFinancialReportsRepository.findByCode(code);
            if (indicator.isEmpty()) {
                continue;
            }

            saved.add(valuesIndicatorsFinancialReportsRepository.save(
                    ValuesIndicatorsFinancialReports.builder()
                            .report(report)
                            .indicatorsFinancialReports(indicator.get())
                            .value(0.0)
                            .previousPeriodValue(0.0)
                            .twoPeriodsAgoValue(0.0)
                            .build()));
        }

        return saved;
    }
}
