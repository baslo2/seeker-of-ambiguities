package report;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import model.Ambiguity;
import model.Ambiguity.Kind;

public class AmbiguityFormatterTest {

    @Test
    void compactsLongAlternative() {
        String compact = AmbiguityFormatter.compact("one two three four five six seven eight nine ten", 20);
        assertEquals(20, compact.length());
        assertTrue(compact.endsWith("..."));
    }

    @Test
    void formatsAmbiguityOnOneLine() {
        Ambiguity ambiguity = new Ambiguity("obj", "'{'",
                List.of("'{' pair (',' pair)* '}'", "'{' '}'"), Kind.DIRECT);
        String formatted = AmbiguityFormatter.formatAmbiguity(ambiguity, 40);
        assertTrue(formatted.contains("rule 'obj'"));
        assertTrue(formatted.contains("shares prefix '{'"));
    }
}
