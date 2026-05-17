package ru.ochakovo.agropulse.service.report;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.ochakovo.agropulse.dto.report.FinancialXmlParsedRow;
import ru.ochakovo.agropulse.entity.dictionary.IndicatorsFinancialReports;
import ru.ochakovo.agropulse.repository.IndicatorsFinancialReportsRepository;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.InputStream;
import java.util.*;

@Component
@RequiredArgsConstructor
public class BuFinancialReportingXmlParser {

    private static final String BALANCE_ROOT = "Баланс";

    private final IndicatorsFinancialReportsRepository indicatorsFinancialReportsRepository;

    public List<FinancialXmlParsedRow> parse(InputStream xml) throws XMLStreamException {
        List<IndicatorsFinancialReports> indicators = indicatorsFinancialReportsRepository.findAllForXmlImport();

        XMLInputFactory factory = XMLInputFactory.newInstance();
        factory.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, false);
        factory.setProperty(XMLInputFactory.SUPPORT_DTD, false);

        XMLStreamReader reader = factory.createXMLStreamReader(xml);
        Deque<String> stack = new ArrayDeque<>();
        Map<String, FinancialXmlParsedRow> firstByCode = new LinkedHashMap<>();
        boolean insideBalance = false;

        while (reader.hasNext()) {
            int event = reader.next();
            if (event == XMLStreamConstants.START_ELEMENT) {
                String tag = reader.getLocalName();
                if (BALANCE_ROOT.equals(tag)) {
                    insideBalance = true;
                }

                if (insideBalance) {
                    String parent = stack.peek();
                    for (IndicatorsFinancialReports indicator : indicators) {
                        if (firstByCode.containsKey(indicator.getCode())) {
                            continue;
                        }
                        if (!matches(indicator, parent, tag)) {
                            continue;
                        }
                        Double reporting = readAttrDouble(reader, "СумОтч");
                        if (reporting == null) {
                            continue;
                        }
                        firstByCode.put(indicator.getCode(), new FinancialXmlParsedRow(
                                indicator.getCode(),
                                reporting,
                                readAttrDouble(reader, "СумПрдщ"),
                                readAttrDouble(reader, "СумПрдшв")
                        ));
                    }
                }

                stack.push(tag);
            } else if (event == XMLStreamConstants.END_ELEMENT) {
                if (BALANCE_ROOT.equals(reader.getLocalName())) {
                    insideBalance = false;
                }
                if (!stack.isEmpty()) {
                    stack.pop();
                }
            }
        }

        return firstByCode.values().stream()
                .sorted(Comparator.comparing(FinancialXmlParsedRow::indicatorCode))
                .toList();
    }

    private static boolean matches(IndicatorsFinancialReports indicator, String parent, String tag) {
        if (parent == null || indicator.getXmlTag() == null || indicator.getParentTag() == null) {
            return false;
        }
        return indicator.getXmlTag().trim().equals(tag)
                && indicator.getParentTag().trim().equals(parent);
    }

    private static Double readAttrDouble(XMLStreamReader reader, String attrName) {
        String raw = reader.getAttributeValue(null, attrName);
        if (raw == null || raw.isBlank()) {
            return null;
        }
        raw = raw.trim();
        try {
            if (raw.contains(",")) {
                raw = raw.replace(" ", "").replace(',', '.');
            }
            return Double.parseDouble(raw);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
