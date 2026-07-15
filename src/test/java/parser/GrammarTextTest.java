package parser;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class GrammarTextTest {

    @Test
    void removesBlockCommentsBetweenRules() {
        String grammar = """
                grammar Sample;

                first
                    : A
                    ;

                /*
                  section header
                */

                second
                    : B
                    ;
                """;

        String prepared = GrammarText.prepareParserGrammar(grammar);
        assertEquals(false, prepared.contains("section header"));
        assertEquals(true, prepared.contains("first"));
        assertEquals(true, prepared.contains("second"));
    }
}
