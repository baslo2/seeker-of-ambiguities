package parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

public class AlternativeParserTest {

    @Test
    void splitsTopLevelAlternatives() {
        List<String> alternatives = AlternativeParser.split("""
                '{' pair (',' pair)* '}'
                | '{' '}'
                ;
                """);
        assertEquals(2, alternatives.size());
        assertEquals("'{' pair (',' pair)* '}'", alternatives.get(0));
        assertEquals("'{' '}'", alternatives.get(1));
    }

    @Test
    void keepsAlternativesInsideGroups() {
        List<String> alternatives = AlternativeParser.split("(COMMIT | END) WORK");
        assertEquals(1, alternatives.size());
        assertTrue(alternatives.get(0).contains("COMMIT | END"));
    }
}
