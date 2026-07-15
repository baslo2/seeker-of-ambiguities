package parser;

import java.util.LinkedHashSet;
import java.util.Set;

public final class FirstSymbolExtractor {

    private static final String EPSILON = "<ε>";

    private FirstSymbolExtractor() {
    }

    public static Set<String> extract(String alternative) {
        Set<String> symbols = new LinkedHashSet<>();
        String cleaned = GrammarText.stripComments(alternative.strip());
        collectFirstSymbols(cleaned, 0, symbols, false);
        symbols.remove("");
        return symbols;
    }

    private static int collectFirstSymbols(String text, int index, Set<String> symbols, boolean insideOptional) {
        index = skipWhitespace(text, index);
        if (index >= text.length()) {
            if (insideOptional) {
                symbols.add(EPSILON);
            }
            return index;
        }

        char c = text.charAt(index);
        if (c == '(') {
            return collectFromGroup(text, index, symbols, insideOptional);
        }
        if (c == '[') {
            int closing = findMatching(text, index, '[', ']');
            collectFirstSymbols(text.substring(index + 1, closing), 0, symbols, true);
            return collectFirstSymbols(text, closing + 1, symbols, insideOptional);
        }
        if (c == '\'' || c == '"') {
            String literal = readQuotedLiteral(text, index);
            symbols.add(literal);
            int next = index + literal.length();
            addEpsilonForOptionalAtom(text, next, symbols);
            return skipSuffixModifiers(text, next);
        }
        if (Character.isLetter(c) || c == '_') {
            String token = readIdentifier(text, index);
            int next = index + token.length();
            next = skipWhitespace(text, next);
            if (next < text.length() && text.charAt(next) == '=') {
                return collectFirstSymbols(text, next + 1, symbols, insideOptional);
            }
            symbols.add(token);
            addEpsilonForOptionalAtom(text, next, symbols);
            return skipSuffixModifiers(text, next);
        }
        return index + 1;
    }

    private static int collectFromGroup(String text, int index, Set<String> symbols, boolean insideOptional) {
        int depth = 0;
        int segmentStart = index + 1;
        boolean inSingleQuote = false;
        boolean inDoubleQuote = false;

        for (int i = index + 1; i < text.length(); i++) {
            char c = text.charAt(i);
            if (inSingleQuote) {
                if (c == '\'' && !isEscaped(text, i)) {
                    inSingleQuote = false;
                }
                continue;
            }
            if (inDoubleQuote) {
                if (c == '"' && !isEscaped(text, i)) {
                    inDoubleQuote = false;
                }
                continue;
            }
            if (c == '\'') {
                inSingleQuote = true;
                continue;
            }
            if (c == '"') {
                inDoubleQuote = true;
                continue;
            }
            if (c == '(' || c == '[') {
                depth++;
                continue;
            }
            if (c == ')' || c == ']') {
                if (depth == 0) {
                    addSegmentFirstSymbols(text.substring(segmentStart, i), symbols, insideOptional);
                    return i + 1;
                }
                depth--;
                continue;
            }
            if (c == '|' && depth == 0) {
                addSegmentFirstSymbols(text.substring(segmentStart, i), symbols, insideOptional);
                segmentStart = i + 1;
            }
        }
        return text.length();
    }

    private static void addSegmentFirstSymbols(String segment, Set<String> symbols, boolean insideOptional) {
        collectFirstSymbols(segment.strip(), 0, symbols, insideOptional);
    }

    private static void addEpsilonForOptionalAtom(String text, int index, Set<String> symbols) {
        index = skipWhitespace(text, index);
        if (index < text.length() && (text.charAt(index) == '?' || text.charAt(index) == '*')) {
            symbols.add(EPSILON);
        }
    }

    private static int skipSuffixModifiers(String text, int index) {
        index = skipWhitespace(text, index);
        while (index < text.length() && (text.charAt(index) == '?' || text.charAt(index) == '*'
                || text.charAt(index) == '+')) {
            index++;
            index = skipWhitespace(text, index);
        }
        return index;
    }

    private static int skipWhitespace(String text, int index) {
        while (index < text.length() && Character.isWhitespace(text.charAt(index))) {
            index++;
        }
        return index;
    }

    private static String readIdentifier(String text, int index) {
        int end = index;
        while (end < text.length()) {
            char c = text.charAt(end);
            if (Character.isLetterOrDigit(c) || c == '_') {
                end++;
            } else {
                break;
            }
        }
        return text.substring(index, end);
    }

    private static String readQuotedLiteral(String text, int index) {
        char quote = text.charAt(index);
        int end = index + 1;
        while (end < text.length()) {
            char c = text.charAt(end);
            if (c == quote && !isEscaped(text, end)) {
                return text.substring(index, end + 1);
            }
            end++;
        }
        return text.substring(index);
    }

    private static int findMatching(String text, int index, char open, char close) {
        int depth = 0;
        boolean inSingleQuote = false;
        boolean inDoubleQuote = false;

        for (int i = index; i < text.length(); i++) {
            char c = text.charAt(i);
            if (inSingleQuote) {
                if (c == '\'' && !isEscaped(text, i)) {
                    inSingleQuote = false;
                }
                continue;
            }
            if (inDoubleQuote) {
                if (c == '"' && !isEscaped(text, i)) {
                    inDoubleQuote = false;
                }
                continue;
            }
            if (c == '\'') {
                inSingleQuote = true;
                continue;
            }
            if (c == '"') {
                inDoubleQuote = true;
                continue;
            }
            if (c == open) {
                depth++;
                continue;
            }
            if (c == close) {
                depth--;
                if (depth == 0) {
                    return i;
                }
            }
        }
        return text.length() - 1;
    }

    private static boolean isEscaped(String text, int index) {
        int backslashes = 0;
        for (int i = index - 1; i >= 0 && text.charAt(i) == '\\'; i--) {
            backslashes++;
        }
        return backslashes % 2 == 1;
    }
}
