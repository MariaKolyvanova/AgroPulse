package ru.ochakovo.agropulse.service.report;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.ochakovo.agropulse.dto.report.FinancialXmlParsedRow;
import ru.ochakovo.agropulse.entity.dictionary.Indicator;
import ru.ochakovo.agropulse.entity.dictionary.ValueSource;
import ru.ochakovo.agropulse.repository.IndicatorRepository;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.InputStream;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Разбор XML потоком <b>StAX</b> (один проход, без построения DOM): по справочнику {@code indicators}.
 * <ul>
 *     <li><b>Корень раздела</b> — по коду строки ({@code Баланс}, {@code ФинРез}, {@code ДвижениеДен}…).</li>
 *     <li><b>{@code parent_tag}</b> — цепочка реальных предков в XML через «/».</li>
 *     <li><b>{@code xml_tag}</b> — узел с суммами ({@code СумОтч} и др.).</li>
 * </ul>
 * Эквивалентно пути XPath {@code //<корень>//…//<xml_tag>}, но без загрузки всего дерева в память.
 */
@Component
@RequiredArgsConstructor
public class BuFinancialReportingXmlParser {

    private final IndicatorRepository indicatorRepository;

    public List<FinancialXmlParsedRow> parse(InputStream xml) throws XMLStreamException {
        List<Indicator> reported = indicatorRepository.findByValueSource(ValueSource.REPORTED);

        XMLInputFactory factory = XMLInputFactory.newInstance();
        factory.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, false);
        factory.setProperty(XMLInputFactory.SUPPORT_DTD, false);

        XMLStreamReader reader = factory.createXMLStreamReader(xml);
        Deque<String> stack = new ArrayDeque<>();
        /** Первое вхождение в порядке документа — как {@code nodes.item(0)} у XPath. */
        Map<String, FinancialXmlParsedRow> firstByCode = new LinkedHashMap<>();

        while (reader.hasNext()) {
            int event = reader.next();
            if (event == XMLStreamConstants.START_ELEMENT) {
                String localName = reader.getLocalName();
                /* ArrayDeque: push = addFirst — обход итератором «от листа к корню»; для пути нужен порядок «корень → лист». */
                List<String> ancestors = new ArrayList<>(stack.size());
                var it = stack.descendingIterator();
                while (it.hasNext()) {
                    ancestors.add(it.next());
                }

                for (Indicator ind : reported) {
                    if (firstByCode.containsKey(ind.getCode())) {
                        continue;
                    }
                    if (!matchesPath(ind, ancestors, localName)) {
                        continue;
                    }
                    Double sumOtch = readReportingValue(reader, ind);
                    if (sumOtch == null) {
                        continue;
                    }
                    Double sumPrdsh = readAttrDouble(reader, "СумПрдщ");
                    Double sumPred = readAttrDouble(reader, "СумПред");
                    Double previous = sumPrdsh != null ? sumPrdsh : sumPred;
                    Double twoAgo = readAttrDouble(reader, "СумПрдшв");
                    firstByCode.put(ind.getCode(), new FinancialXmlParsedRow(
                            ind.getCode(),
                            sumOtch,
                            previous,
                            twoAgo
                    ));
                }

                stack.push(localName);
            } else if (event == XMLStreamConstants.END_ELEMENT) {
                if (!stack.isEmpty()) {
                    stack.pop();
                }
            }
        }

        return firstByCode.values().stream()
                .sorted(Comparator.comparing(FinancialXmlParsedRow::indicatorCode))
                .toList();
    }

    /**
     * Совпадение с шаблоном «последний в стеке корень раздела → по порядку сегменты {@code parent_tag} → лист {@code xml_tag}».
     */
    private static boolean matchesPath(Indicator ind, List<String> ancestors, String localName) {
        if (!ind.getXmlTag().trim().equals(localName)) {
            return false;
        }
        String root = sectionFromCode(ind);
        int rootIdx = lastIndexOf(ancestors, root);
        if (rootIdx < 0) {
            return false;
        }
        int pos = rootIdx + 1;
        for (String seg : parentPathSegments(ind)) {
            int found = -1;
            for (int i = pos; i < ancestors.size(); i++) {
                if (seg.equals(ancestors.get(i))) {
                    found = i;
                    break;
                }
            }
            if (found < 0) {
                return false;
            }
            pos = found + 1;
        }
        return true;
    }

    private static int lastIndexOf(List<String> list, String value) {
        for (int i = list.size() - 1; i >= 0; i--) {
            if (value.equals(list.get(i))) {
                return i;
            }
        }
        return -1;
    }

    private static List<String> parentPathSegments(Indicator ind) {
        String p = trimToNull(ind.getParentTag());
        if (p == null) {
            return List.of();
        }
        List<String> out = new ArrayList<>();
        for (String part : p.split("/")) {
            String s = trimToNull(part);
            if (s != null) {
                out.add(s);
            }
        }
        return out;
    }

    private static String trimToNull(String s) {
        if (s == null) {
            return null;
        }
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }

    private static String sectionFromCode(Indicator ind) {
        try {
            int code = Integer.parseInt(ind.getCode().trim());
            if (code >= 1100 && code <= 1999) {
                return "Баланс";
            }
            if (code >= 2000 && code <= 2999) {
                return "ФинРез";
            }
            if (code >= 3000 && code <= 3999) {
                return "ОтчетИзмКап";
            }
            if (code >= 4000 && code <= 4999) {
                return "ДвижениеДен";
            }
        } catch (NumberFormatException ignored) {
        }
        return "Документ";
    }

    /**
     * В ОФР/балансе/ОДДС суммы в {@code СумОтч}; в отчёте об изменении капитала часто только {@code Итог}.
     */
    private static Double readReportingValue(XMLStreamReader reader, Indicator ind) {
        Double v = readAttrDouble(reader, "СумОтч");
        if (v != null) {
            return v;
        }
        if ("ОтчетИзмКап".equals(sectionFromCode(ind))) {
            return readAttrDouble(reader, "Итог");
        }
        return null;
    }

    private static Double readAttrDouble(XMLStreamReader reader, String attrName) {
        String v = reader.getAttributeValue(null, attrName);
        if (v == null || v.isBlank()) {
            return null;
        }
        v = v.trim();
        try {
            if (v.contains(",")) {
                v = v.replace(" ", "").replace(',', '.');
            }
            return Double.parseDouble(v);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
