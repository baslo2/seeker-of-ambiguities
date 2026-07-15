package parser;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import model.Rule;

public final class RuleParser {

    private static final Pattern RULE_HEADER = Pattern.compile("(?m)^([a-z][a-zA-Z0-9_]*)\\s*:");

    private RuleParser() {
    }

    public static Map<String, Rule> parse(String context) {
        String prepared = GrammarText.prepareParserGrammar(context);
        Matcher matcher = RULE_HEADER.matcher(prepared);
        List<RuleHeader> headers = new ArrayList<>();
        while (matcher.find()) {
            headers.add(new RuleHeader(matcher.group(1), matcher.start(), matcher.end()));
        }

        Map<String, Rule> rules = new LinkedHashMap<>();
        for (int i = 0; i < headers.size(); i++) {
            RuleHeader header = headers.get(i);
            int bodyEnd = i + 1 < headers.size() ? headers.get(i + 1).headerStart() : prepared.length();
            Rule rule = new Rule(header.name());
            rule.setBody(normalizeBody(prepared.substring(header.bodyStart(), bodyEnd)));
            rules.put(header.name(), rule);
        }
        return rules;
    }

    private static String normalizeBody(String body) {
        String normalized = body.strip();
        if (normalized.endsWith(";")) {
            normalized = normalized.substring(0, normalized.length() - 1).strip();
        }
        return normalized;
    }

    private record RuleHeader(String name, int headerStart, int bodyStart) {
    }
}
