package parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;

import org.junit.jupiter.api.Test;

public class FirstSymbolExtractorTest {

    @Test
    void extractsLiteralPrefix() {
        assertEquals(Set.of("'{'"), FirstSymbolExtractor.extract("'{' pair (',' pair)* '}'"));
    }

    @Test
    void extractsRuleReference() {
        assertEquals(Set.of("value"), FirstSymbolExtractor.extract("value EOF"));
    }

    @Test
    void extractsGroupedPrefixes() {
        Set<String> symbols = FirstSymbolExtractor.extract("(COMMIT | END | ABORT) WORK");
        assertEquals(Set.of("COMMIT", "END", "ABORT"), symbols);
    }

    @Test
    void skipsLabelBeforeRuleReference() {
        assertEquals(Set.of("identifier"), FirstSymbolExtractor.extract("name=identifier EOF"));
    }

    @Test
    void includesEpsilonForOptionalPrefix() {
        Set<String> symbols = FirstSymbolExtractor.extract("BOM? SEMI_COLON* statement");
        assertTrue(symbols.contains("<ε>"));
        assertTrue(symbols.contains("BOM"));
    }
}
