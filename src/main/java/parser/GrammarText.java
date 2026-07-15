package parser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class GrammarText {

    private static final Pattern PARSER_GRAMMAR = Pattern.compile("(?m)^(?:lexer|parser)?\\s*grammar\\s+\\w+\\s*;\\s*");
    private static final Pattern OPTIONS_BLOCK = Pattern.compile("(?m)^options\\s*\\{");
    private static final Pattern ANTLR_ACTION = Pattern.compile("(?m)^@[A-Za-z]+\\s*\\{");

    private GrammarText() {
    }

    public static String stripComments(String text) {
        StringBuilder result = new StringBuilder(text.length());
        int index = 0;
        while (index < text.length()) {
            if (startsWith(text, index, "/*")) {
                int end = text.indexOf("*/", index + 2);
                index = end < 0 ? text.length() : end + 2;
                result.append('\n');
                continue;
            }
            if (startsWith(text, index, "//")) {
                int end = text.indexOf('\n', index + 2);
                index = end < 0 ? text.length() : end;
                result.append('\n');
                continue;
            }
            result.append(text.charAt(index));
            index++;
        }
        return result.toString();
    }

    public static String prepareParserGrammar(String text) {
        String prepared = stripComments(text);
        prepared = PARSER_GRAMMAR.matcher(prepared).replaceFirst("");
        prepared = stripBlockStartingWith(prepared, OPTIONS_BLOCK);
        prepared = stripAntlrActions(prepared);
        return prepared.strip();
    }

    private static String stripAntlrActions(String text) {
        String result = text;
        Matcher matcher = ANTLR_ACTION.matcher(result);
        while (matcher.find()) {
            int blockStart = matcher.end() - 1;
            int blockEnd = findMatchingBrace(result, blockStart);
            result = result.substring(0, matcher.start()) + result.substring(blockEnd + 1);
            matcher = ANTLR_ACTION.matcher(result);
        }
        return result;
    }

    private static String stripBlockStartingWith(String text, Pattern blockStartPattern) {
        Matcher matcher = blockStartPattern.matcher(text);
        if (!matcher.find()) {
            return text;
        }
        int blockStart = matcher.end() - 1;
        int blockEnd = findMatchingBrace(text, blockStart);
        return text.substring(0, matcher.start()) + text.substring(blockEnd + 1);
    }

    static int findMatchingBrace(String text, int openBraceIndex) {
        int depth = 0;
        boolean inSingleQuote = false;
        boolean inDoubleQuote = false;

        for (int i = openBraceIndex; i < text.length(); i++) {
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
            if (c == '{') {
                depth++;
                continue;
            }
            if (c == '}') {
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

    private static boolean startsWith(String text, int index, String value) {
        return text.startsWith(value, index);
    }
}
