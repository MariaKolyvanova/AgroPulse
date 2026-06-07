package ru.ochakovo.agropulse.service.report.xml;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.ochakovo.agropulse.dto.report.xml.FinancialXmlParsedRow;
import ru.ochakovo.agropulse.entity.dictionary.IndicatorsFinancialReports;
import ru.ochakovo.agropulse.repository.xml.IndicatorsFinancialReportsRepository;

import javax.xml.stream.XMLStreamException;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class XmlParserUnitTest {

    @Mock
    IndicatorsFinancialReportsRepository indicatorsFinancialReportsRepository;

    XmlParser xmlParser;

    @BeforeEach
    void setUp() {
        xmlParser = new XmlParser(indicatorsFinancialReportsRepository);
        when(indicatorsFinancialReportsRepository.findAllForXmlImport()).thenReturn(indicators());
    }

    @Test
    void parse_extractsFinResAndCashFlow_skipsVPOkOPP() throws Exception {
        String xml = """
                <?xml version="1.0" encoding="UTF-8"?>
                <Документ>
                    <Баланс>
                        <Актив>
                            <ОбА>
                                <Запасы СумОтч="4061192" СумПрдщ="4910716" СумПрдшв="5109835">
                                    <ВПокОПП НаимПок="сырье" СумОтч="2166046"/>
                                </Запасы>
                            </ОбА>
                        </Актив>
                    </Баланс>
                    <ФинРез>
                        <Выруч СумОтч="24220767" СумПред="19932010"/>
                        <ЧистПрибУб СумОтч="598299" СумПред="486423"/>
                    </ФинРез>
                    <ДвижениеДен>
                        <ТекОпер>
                            <СальдоТек СумОтч="2113441" СумПред="965443"/>
                        </ТекОпер>
                        <СальдоОтч СумОтч="573597" СумПред="-202305"/>
                    </ДвижениеДен>
                </Документ>
                """;

        Map<String, FinancialXmlParsedRow> rows = parse(xml);

        assertThat(rows.get("1210").reportingValue()).isEqualTo(4_061_192.0);
        assertThat(rows.get("2110").reportingValue()).isEqualTo(24_220_767.0);
        assertThat(rows.get("2110").previousPeriodValue()).isEqualTo(19_932_010.0);
        assertThat(rows.get("2400").reportingValue()).isEqualTo(598_299.0);
        assertThat(rows.get("4100").reportingValue()).isEqualTo(2_113_441.0);
        assertThat(rows.get("4400").reportingValue()).isEqualTo(573_597.0);
    }

    @Test
    void parse_capitalMovementUsesItogAttribute() throws Exception {
        String xml = """
                <?xml version="1.0" encoding="UTF-8"?>
                <Документ>
                    <ОтчетИзмКап>
                        <ДвиженКап>
                            <ОтчетГод>
                                <УвеличКапитал>
                                    <ЧистПриб НераспПриб="598299" Итог="598299"/>
                                </УвеличКапитал>
                            </ОтчетГод>
                        </ДвиженКап>
                    </ОтчетИзмКап>
                </Документ>
                """;

        Map<String, FinancialXmlParsedRow> rows = parse(xml);

        assertThat(rows.get("3311").reportingValue()).isEqualTo(598_299.0);
    }

    private Map<String, FinancialXmlParsedRow> parse(String xml) throws XMLStreamException {
        var input = new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8));
        return xmlParser.parse(input).stream()
                .collect(Collectors.toMap(FinancialXmlParsedRow::indicatorCode, Function.identity()));
    }

    private static List<IndicatorsFinancialReports> indicators() {
        return List.of(
                indicator("1210", "ОбА", "Запасы"),
                indicator("2110", "ФинРез", "Выруч"),
                indicator("2400", "ФинРез", "ЧистПрибУб"),
                indicator("4100", "ТекОпер", "СальдоТек"),
                indicator("4400", "ДвижениеДен", "СальдоОтч"),
                indicator("3311", "ОтчетГод", "ЧистПриб")
        );
    }

    private static IndicatorsFinancialReports indicator(String code, String parentTag, String xmlTag) {
        return IndicatorsFinancialReports.builder()
                .code(code)
                .parentTag(parentTag)
                .xmlTag(xmlTag)
                .indicatorName(code)
                .build();
    }
}
