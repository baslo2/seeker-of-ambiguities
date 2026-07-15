package finder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import model.Ambiguity;
import model.Ambiguity.Kind;
import model.Rule;
import parser.AlternativeParser;
import parser.FirstSymbolExtractor;

public final class AmbiguityFinder {

    private static final String EPSILON = "<ε>";

    private AmbiguityFinder() {
    }

    public static List<Ambiguity> find(Map<String, Rule> rules) {
        return mergeResults(findDirect(rules), findExpanded(rules));
    }

    public static List<Ambiguity> findDirect(Map<String, Rule> rules) {
        return findByKind(rules, Kind.DIRECT, FirstSymbolExtractor::extract);
    }

    public static List<Ambiguity> findExpanded(Map<String, Rule> rules) {
        return findByKind(rules, Kind.EXPANDED, alternative -> expandRuleReferences(alternative, rules));
    }

    public static List<Ambiguity> findInRule(Rule rule, Map<String, Rule> rules) {
        List<Ambiguity> ambiguities = new ArrayList<>();
        ambiguities.addAll(findInRule(rule, rules, Kind.DIRECT, FirstSymbolExtractor::extract));
        ambiguities.addAll(findInRule(rule, rules, Kind.EXPANDED, alternative -> expandRuleReferences(alternative, rules)));
        return mergeResults(
                ambiguities.stream().filter(ambiguity -> ambiguity.getKind() == Kind.DIRECT).toList(),
                ambiguities.stream().filter(ambiguity -> ambiguity.getKind() == Kind.EXPANDED).toList());
    }

    private static List<Ambiguity> mergeResults(List<Ambiguity> direct, List<Ambiguity> expanded) {
        Set<String> directKeys = new HashSet<>();
        for (Ambiguity ambiguity : direct) {
            directKeys.add(ambiguityKey(ambiguity));
        }

        List<Ambiguity> merged = new ArrayList<>(direct);
        for (Ambiguity ambiguity : expanded) {
            if (!directKeys.contains(ambiguityKey(ambiguity))) {
                merged.add(ambiguity);
            }
        }
        return merged;
    }

    private static String ambiguityKey(Ambiguity ambiguity) {
        return ambiguity.getRuleName() + '\0' + ambiguity.getFirstSymbol();
    }

    private static List<Ambiguity> findByKind(Map<String, Rule> rules, Kind kind,
            Function<String, Set<String>> symbolExtractor) {
        List<Ambiguity> ambiguities = new ArrayList<>();
        for (Rule rule : rules.values()) {
            ambiguities.addAll(findInRule(rule, rules, kind, symbolExtractor));
        }
        return ambiguities;
    }

    private static List<Ambiguity> findInRule(Rule rule, Map<String, Rule> rules, Kind kind,
            Function<String, Set<String>> symbolExtractor) {
        List<String> alternatives = AlternativeParser.split(rule.getBody());
        if (alternatives.size() < 2) {
            return List.of();
        }

        Map<String, List<Integer>> symbolToAlternativeIndexes = new HashMap<>();
        for (int i = 0; i < alternatives.size(); i++) {
            Set<String> symbols = symbolExtractor.apply(alternatives.get(i));
            for (String symbol : symbols) {
                if (shouldSkipSymbol(symbol, kind)) {
                    continue;
                }
                symbolToAlternativeIndexes.computeIfAbsent(symbol, key -> new ArrayList<>()).add(i);
            }
        }

        List<Ambiguity> ambiguities = new ArrayList<>();
        for (Map.Entry<String, List<Integer>> entry : symbolToAlternativeIndexes.entrySet()) {
            List<Integer> indexes = entry.getValue().stream().distinct().sorted().toList();
            if (indexes.size() < 2) {
                continue;
            }
            List<String> conflictingAlternatives = indexes.stream()
                    .map(alternatives::get)
                    .toList();
            ambiguities.add(new Ambiguity(rule.getName(), entry.getKey(), conflictingAlternatives, kind));
        }
        return ambiguities;
    }

    private static Set<String> expandRuleReferences(String alternative, Map<String, Rule> rules) {
        Set<String> expanded = new LinkedHashSet<>();
        for (String symbol : FirstSymbolExtractor.extract(alternative)) {
            if (symbol.equals(EPSILON) || isLiteralOrLexerToken(symbol) || !rules.containsKey(symbol)) {
                continue;
            }
            Rule referencedRule = rules.get(symbol);
            for (String nestedAlternative : AlternativeParser.split(referencedRule.getBody())) {
                expanded.addAll(FirstSymbolExtractor.extract(nestedAlternative));
            }
        }
        return expanded;
    }

    private static boolean shouldSkipSymbol(String symbol, Kind kind) {
        return EPSILON.equals(symbol);
    }

    private static boolean isLiteralOrLexerToken(String symbol) {
        return symbol.startsWith("'") || symbol.startsWith("\"") || symbol.equals(symbol.toUpperCase());
    }
}
