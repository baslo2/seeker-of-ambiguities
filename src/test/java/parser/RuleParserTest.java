package parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;

import reader.TestUtils;

public class RuleParserTest {

    private static final String RULE_ARR = """
Rule{name='arr', body=''[' value (',' value)* ']'
    | '[' ']''}""";

    @Test
    void testRulesSize() {
        var rules = TestUtils.getRules(TestUtils.JSON);
        assertEquals(5, rules.size());
    }

    @Test
    void testGetRule() {
        assertEquals(RULE_ARR, TestUtils.getRules(TestUtils.JSON).get("arr").toString());
    }

    @Test
    void stripsCommentsBetweenRules() {
        var rules = TestUtils.getRules(TestUtils.SQL);
        var indirection = rules.get("indirection");

        assertEquals(3, AlternativeParser.split(indirection.getBody()).size());
        assertFalse(indirection.getBody().contains("11.21"));
        assertFalse(indirection.getBody().contains("data types"));
    }
}
