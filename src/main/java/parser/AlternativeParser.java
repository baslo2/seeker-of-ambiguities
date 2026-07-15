package parser;

import java.util.ArrayList;
import java.util.List;

public final class AlternativeParser {

    private AlternativeParser() {
    }

    public static List<String> split(String body) {
        String trimmed = stripTrailingSemicolon(body.strip());
        List<String> alternatives = new ArrayList<>();
        int depth = 0;
        int start = 0;
        boolean inSingleQuote = false;
        boolean inDoubleQuote = false;

        for (int i = 0; i < trimmed.length(); i++) {
            char c = trimmed.charAt(i);
            if (inSingleQuote) {
                if (c == '\'' && !isEscaped(trimmed, i)) {
                    inSingleQuote = false;
                }
                continue;
            }
            if (inDoubleQuote) {
                if (c == '"' && !isEscaped(trimmed, i)) {
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
                depth--;
                continue;
            }
            if (c == '|' && depth == 0) {
                addIfNotBlank(alternatives, trimmed.substring(start, i));
                start = i + 1;
            }
        }
        addIfNotBlank(alternatives, trimmed.substring(start));
        return alternatives;
    }

    private static String stripTrailingSemicolon(String body) {
        if (body.endsWith(";")) {
            return body.substring(0, body.length() - 1).strip();
        }
        return body;
    }

    private static void addIfNotBlank(List<String> alternatives, String value) {
        String stripped = value.strip();
        if (!stripped.isEmpty()) {
            alternatives.add(stripped);
        }
    }

    private static boolean isEscaped(String text, int index) {
        int backslashes = 0;
        for (int i = index - 1; i >= 0 && text.charAt(i) == '\\'; i--) {
            backslashes++;
        }
        return backslashes % 2 == 1;
    }
}
