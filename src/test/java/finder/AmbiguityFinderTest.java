package finder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import model.Ambiguity;
import model.Ambiguity.Kind;
import model.Rule;
import parser.RuleParser;
import reader.G4Reader;
import reader.TestUtils;

public class AmbiguityFinderTest {

    @Test
    void findsAmbiguitiesInJsonGrammar() {
        Map<String, Rule> rules = TestUtils.getRules(TestUtils.JSON);
        List<Ambiguity> ambiguities = AmbiguityFinder.findDirect(rules);

        assertEquals(2, ambiguities.size());
        assertTrue(containsRule(ambiguities, "obj", "'{'", Kind.DIRECT));
        assertTrue(containsRule(ambiguities, "arr", "'['", Kind.DIRECT));
    }

    @Test
    void returnsEmptyForSingleAlternativeRule() {
        Map<String, Rule> rules = TestUtils.getRules(TestUtils.JSON);
        List<Ambiguity> ambiguities = AmbiguityFinder.findInRule(rules.get("pair"), rules);
        assertTrue(ambiguities.isEmpty());
    }

    @Test
    void expandedAmbiguitiesIgnoreDirectLiteralPrefixes() {
        Map<String, Rule> rules = TestUtils.getRules(TestUtils.JSON);
        List<Ambiguity> expanded = AmbiguityFinder.findExpanded(rules);
        assertTrue(expanded.isEmpty());
    }

    @Test
    void findsExpandedAmbiguityThroughRuleReferences() {
        String grammar = G4Reader.readParser(TestUtils.RESOUCE + "/ExpandedSample.g4");
        Map<String, Rule> rules = RuleParser.parse(grammar);
        List<Ambiguity> expanded = AmbiguityFinder.findExpanded(rules);

        assertEquals(1, expanded.size());
        assertTrue(containsRule(expanded, "start", "TOKEN", Kind.EXPANDED));
    }

    @Test
    void mergedResultsSkipExpandedDuplicatesOfDirect() {
        Map<String, Rule> rules = TestUtils.getRules(TestUtils.JSON);
        List<Ambiguity> merged = AmbiguityFinder.find(rules);

        assertEquals(2, merged.size());
        assertTrue(merged.stream().allMatch(ambiguity -> ambiguity.getKind() == Kind.DIRECT));
    }

    private static boolean containsRule(List<Ambiguity> ambiguities, String ruleName, String symbol, Kind kind) {
        return ambiguities.stream()
                .anyMatch(ambiguity -> ambiguity.getKind() == kind
                        && ambiguity.getRuleName().equals(ruleName)
                        && ambiguity.getFirstSymbol().equals(symbol));
    }
}
