package ru.ochakovo.agropulse.service.report;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ochakovo.agropulse.dto.report.FinancialXmlParsedRow;
import ru.ochakovo.agropulse.entity.data.IndicatorValue;
import ru.ochakovo.agropulse.entity.data.Report;
import ru.ochakovo.agropulse.repository.IndicatorRepository;
import ru.ochakovo.agropulse.repository.IndicatorValueRepository;

import javax.xml.stream.XMLStreamException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FinancialReportIndicatorImportService {

    private final BuFinancialReportingXmlParser buFinancialReportingXmlParser;
    private final IndicatorRepository indicatorRepository;
    private final IndicatorValueRepository indicatorValueRepository;

    /**
     * Читает XML и сохраняет значения показателей по отчёту (старые строки по этому отчёту удаляются).
     */
    @Transactional
    public List<IndicatorValue> importFromXml(Report report, InputStream xml) throws XMLStreamException {
        List<FinancialXmlParsedRow> rows = buFinancialReportingXmlParser.parse(xml);
        indicatorValueRepository.deleteAllByReport_Id(report.getId());

        List<IndicatorValue> saved = new ArrayList<>();
        for (FinancialXmlParsedRow row : rows) {
            var indicator = indicatorRepository.findByCode(row.indicatorCode())
                    .orElseThrow(() -> new IllegalStateException("Нет показателя с кодом " + row.indicatorCode()));
                    
            IndicatorValue entity = IndicatorValue.builder()
                    .report(report)
                    .indicator(indicator)
                    .value(row.reportingValue())
                    .previousPeriodValue(row.previousPeriodValue())
                    .twoPeriodsAgoValue(row.twoPeriodsAgoValue())
                    .build();
            saved.add(indicatorValueRepository.save(entity));
        }
        return saved;
    }
}
