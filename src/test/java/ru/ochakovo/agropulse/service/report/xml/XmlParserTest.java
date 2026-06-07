package ru.ochakovo.agropulse.service.report.xml;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.ochakovo.agropulse.IntegrationTestBase;
import ru.ochakovo.agropulse.annotation.IT;
import ru.ochakovo.agropulse.dto.report.xml.FinancialXmlParsedRow;

import javax.xml.stream.XMLStreamException;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@IT
class XmlParserTest extends IntegrationTestBase {

    private static final String SAMPLE_BALANCE = "xml/sample-balance.xml";

    @Autowired
    XmlParser xmlParser;

    @Test
    void parseSampleBalance_extractsKeyIndicatorsWithAllPeriods() throws Exception {
        Map<String, FinancialXmlParsedRow> rows = parseToMap(SAMPLE_BALANCE);

        assertThat(rows).containsKeys("1210", "1250", "1600", "1700", "1200", "1500");

        FinancialXmlParsedRow inventories = rows.get("1210");
        assertThat(inventories.reportingValue()).isEqualTo(800_000.0);
        assertThat(inventories.previousPeriodValue()).isEqualTo(750_000.0);
        assertThat(inventories.twoPeriodsAgoValue()).isEqualTo(700_000.0);

        assertThat(rows.get("1600").reportingValue()).isEqualTo(10_000_000.0);
        assertThat(rows.get("1200").reportingValue()).isEqualTo(6_000_000.0);
        assertThat(rows.get("1500").reportingValue()).isEqualTo(4_000_000.0);
    }

    @Test
    void parseSampleBalance_returnsRowsSortedByIndicatorCode() throws Exception {
        var codes = xmlParser.parse(open(SAMPLE_BALANCE)).stream()
                .map(FinancialXmlParsedRow::indicatorCode)
                .toList();

        assertThat(codes).isSorted();
    }

    @Test
    void parseDecimalWithComma_readsRussianNumberFormat() throws Exception {
        String xml = """
                <?xml version="1.0" encoding="UTF-8"?>
                <Документ>
                    <Баланс>
                        <Актив>
                            <ОбА>
                                <Запасы СумОтч="1 234,56" СумПрдщ="100,5" СумПрдшв="50"/>
                            </ОбА>
                        </Актив>
                    </Баланс>
                </Документ>
                """;

        Map<String, FinancialXmlParsedRow> rows = parseToMapFromContent(xml);

        assertThat(rows.get("1210").reportingValue()).isEqualTo(1234.56);
        assertThat(rows.get("1210").previousPeriodValue()).isEqualTo(100.5);
        assertThat(rows.get("1210").twoPeriodsAgoValue()).isEqualTo(50.0);
    }

    @Test
    void parsePartialBalance_extractsOnlyPresentNodes() throws Exception {
        Map<String, FinancialXmlParsedRow> rows = parseToMap("xml/sample-balance-partial.xml");

        assertThat(rows).containsKeys("1600", "1700", "1200", "1500");
        assertThat(rows).doesNotContainKey("1210");
        assertThat(rows.get("1600").reportingValue()).isEqualTo(10_000_000.0);
    }

    private Map<String, FinancialXmlParsedRow> parseToMap(String classpathResource) throws Exception {
        return parseToMap(open(classpathResource));
    }

    private Map<String, FinancialXmlParsedRow> parseToMapFromContent(String xmlContent) throws Exception {
        try (InputStream in = new ByteArrayInputStream(xmlContent.getBytes(StandardCharsets.UTF_8))) {
            return parseToMap(in);
        }
    }

    private Map<String, FinancialXmlParsedRow> parseToMap(InputStream xml) throws XMLStreamException {
        return xmlParser.parse(xml).stream()
                .collect(Collectors.toMap(FinancialXmlParsedRow::indicatorCode, Function.identity()));
    }

    private InputStream open(String classpathResource) {
        InputStream stream = getClass().getClassLoader().getResourceAsStream(classpathResource);
        if (stream == null) {
            throw new IllegalStateException("Resource not found: " + classpathResource);
        }
        return stream;
    }
}
