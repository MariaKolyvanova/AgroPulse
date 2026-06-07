package ru.ochakovo.agropulse.service.report.xml;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.ochakovo.agropulse.dto.report.xml.FinancialXmlParsedRow;
import ru.ochakovo.agropulse.entity.dictionary.IndicatorsFinancialReports;
import ru.ochakovo.agropulse.repository.xml.IndicatorsFinancialReportsRepository;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.InputStream;
import java.util.*;

@Component
@RequiredArgsConstructor
public class XmlParser {

    private static final Set<String> SKIP_TAGS = Set.of("ВПокОПП", "ДопПокОП");

    private final IndicatorsFinancialReportsRepository indicatorsFinancialReportsRepository;

    public List<FinancialXmlParsedRow> parse(InputStream xml) throws XMLStreamException {
        List<IndicatorsFinancialReports> indicators = indicatorsFinancialReportsRepository.findAllForXmlImport();

        XMLInputFactory factory = XMLInputFactory.newInstance();
        factory.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, false);
        factory.setProperty(XMLInputFactory.SUPPORT_DTD, false);

        XMLStreamReader reader = factory.createXMLStreamReader(xml);
        List<String> ancestorsStack = new ArrayList<>();
        Map<String, FinancialXmlParsedRow> firstByCode = new LinkedHashMap<>();

        while (reader.hasNext()) {
            int event = reader.next();
            if (event == XMLStreamConstants.START_ELEMENT) {
                String tag = reader.getLocalName();
                List<String> ancestors = List.copyOf(ancestorsStack);

                if (!SKIP_TAGS.contains(tag)) {
                    for (IndicatorsFinancialReports indicator : indicators) {
                        if (firstByCode.containsKey(indicator.getCode())) {
                            continue;
                        }
                        if (!matches(indicator, ancestors, tag)) {
                            continue;
                        }
                        PeriodValues values = readPeriodValues(reader);
                        if (values.reporting() == null) {
                            continue;
                        }
                        firstByCode.put(indicator.getCode(), new FinancialXmlParsedRow(
                                indicator.getCode(),
                                values.reporting(),
                                values.previous(),
                                values.twoPeriodsAgo()
                        ));
                    }
                }

                ancestorsStack.add(tag);
            } else if (event == XMLStreamConstants.END_ELEMENT) {
                if (!ancestorsStack.isEmpty()) {
                    ancestorsStack.remove(ancestorsStack.size() - 1);
                }
            }
        }

        return firstByCode.values().stream()
                .sorted(Comparator.comparing(FinancialXmlParsedRow::indicatorCode))
                .toList();
    }

    private static boolean matches(IndicatorsFinancialReports indicator, List<String> ancestors, String tag) {
        String xmlTag = trimToNull(indicator.getXmlTag());
        if (xmlTag == null || !xmlTag.equals(tag)) {
            return false;
        }

        String section = sectionFromCode(indicator.getCode());
        int sectionIdx = lastIndexOf(ancestors, section);
        if (sectionIdx < 0) {
            return false;
        }

        int searchFrom = sectionIdx;
        for (String segment : parentPathSegments(indicator.getParentTag())) {
            int idx = indexOfFrom(ancestors, segment, searchFrom);
            if (idx < 0) {
                return false;
            }
            searchFrom = idx + 1;
        }
        return true;
    }

    private static String sectionFromCode(String code) {
        try {
            int value = Integer.parseInt(code.trim());
            if (value == 1000) {
                return "Документ";
            }
            if (value == 2000) {
                return "Документ";
            }
            if (value == 3000) {
                return "Документ";
            }
            if (value == 4000) {
                return "Документ";
            }
            if (value >= 1100 && value <= 1999) {
                return "Баланс";
            }
            if (value >= 2100 && value <= 2999) {
                return "ФинРез";
            }
            if (value >= 3000 && value <= 3999) {
                return "ОтчетИзмКап";
            }
            if (value >= 4000 && value <= 4999) {
                return "ДвижениеДен";
            }
        } catch (NumberFormatException ignored) {
        }
        return "Документ";
    }

    private static List<String> parentPathSegments(String parentTag) {
        String raw = trimToNull(parentTag);
        if (raw == null) {
            return List.of();
        }
        return Arrays.stream(raw.split("/"))
                .map(String::trim)
                .filter(segment -> !segment.isEmpty())
                .toList();
    }

    private static int lastIndexOf(List<String> items, String value) {
        for (int i = items.size() - 1; i >= 0; i--) {
            if (value.equals(items.get(i))) {
                return i;
            }
        }
        return -1;
    }

    private static int indexOfFrom(List<String> items, String value, int fromIndex) {
        for (int i = fromIndex; i < items.size(); i++) {
            if (value.equals(items.get(i))) {
                return i;
            }
        }
        return -1;
    }

    private static PeriodValues readPeriodValues(XMLStreamReader reader) {
        Double reporting = readAttrDouble(reader, "СумОтч");
        if (reporting == null) {
            reporting = readAttrDouble(reader, "На31ДекОтч");
        }
        if (reporting == null) {
            reporting = readAttrDouble(reader, "Итог");
        }

        Double previous = readAttrDouble(reader, "СумПрдщ");
        if (previous == null) {
            previous = readAttrDouble(reader, "СумПред");
        }
        if (previous == null) {
            previous = readAttrDouble(reader, "На31ДекПред");
        }

        Double twoPeriodsAgo = readAttrDouble(reader, "СумПрдшв");
        if (twoPeriodsAgo == null) {
            twoPeriodsAgo = readAttrDouble(reader, "На31ДекПрПред");
        }

        return new PeriodValues(reporting, previous, twoPeriodsAgo);
    }

    private static String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
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

    private record PeriodValues(Double reporting, Double previous, Double twoPeriodsAgo) {
    }
}
