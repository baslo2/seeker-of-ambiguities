package report;

import java.util.Locale;

public final class AmbiguityTreeLabels {

    public enum Language {
        EN,
        RU
    }

    private final Language language;

    public AmbiguityTreeLabels(Language language) {
        this.language = language;
    }

    public static AmbiguityTreeLabels of(Language language) {
        return new AmbiguityTreeLabels(language);
    }

    public String title() {
        return language == Language.RU ? "\u041e\u0442\u0447\u0451\u0442 \u043e \u043d\u0435\u043e\u0434\u043d\u043e\u0437\u043d\u0430\u0447\u043d\u043e\u0441\u0442\u044f\u0445 ANTLR4"
                : "Ambiguity report (ANTLR4 grammar)";
    }

    public String grammar() {
        return language == Language.RU ? "\u0413\u0440\u0430\u043c\u043c\u0430\u0442\u0438\u043a\u0430" : "Grammar";
    }

    public String rules() {
        return language == Language.RU ? "\u041f\u0440\u0430\u0432\u0438\u043b" : "Rules";
    }

    public String found() {
        return language == Language.RU ? "\u041d\u0430\u0439\u0434\u0435\u043d\u043e" : "Found";
    }

    public String showing() {
        return language == Language.RU ? "\u041f\u043e\u043a\u0430\u0437\u0430\u043d\u043e" : "Showing";
    }

    public String firstOf() {
        return language == Language.RU ? "\u0438\u0437" : "of";
    }

    public String firstWord() {
        return language == Language.RU ? "\u043f\u0435\u0440\u0432\u044b\u0435" : "first";
    }

    public String noAmbiguities() {
        return language == Language.RU ? "\u041d\u0435\u043e\u0434\u043d\u043e\u0437\u043d\u0430\u0447\u043d\u043e\u0441\u0442\u0435\u0439 \u043d\u0435 \u043d\u0430\u0439\u0434\u0435\u043d\u043e."
                : "No ambiguities found.";
    }

    public String rule() {
        return language == Language.RU ? "\u043f\u0440\u0430\u0432\u0438\u043b\u043e" : "rule";
    }

    public String sharedPrefix() {
        return language == Language.RU ? "\u043e\u0431\u0449\u0438\u0439 \u043f\u0440\u0435\u0444\u0438\u043a\u0441" : "shared prefix";
    }

    public String alternative() {
        return language == Language.RU ? "\u0432\u0430\u0440\u0438\u0430\u043d\u0442" : "alternative";
    }

    public String direct() {
        return language == Language.RU ? "\u043f\u0440\u044f\u043c\u0430\u044f" : "direct";
    }

    public String viaReferences() {
        return language == Language.RU ? "\u0447\u0435\u0440\u0435\u0437 \u0441\u0441\u044b\u043b\u043a\u0438" : "via references";
    }

    public String directCount() {
        return language == Language.RU ? "\u043f\u0440\u044f\u043c\u044b\u0445" : "direct";
    }

    public String viaRuleRefsCount() {
        return language == Language.RU ? "\u0447\u0435\u0440\u0435\u0437 \u0441\u0441\u044b\u043b\u043a\u0438" : "via rule refs";
    }

    public String affectedRules() {
        return language == Language.RU ? "\u043f\u0440\u0430\u0432\u0438\u043b \u0437\u0430\u0442\u0440\u043e\u043d\u0443\u0442\u043e" : "affected rules";
    }

    public String humanizeSymbol(String symbol) {
        if ("<ε>".equals(symbol)) {
            return language == Language.RU ? "\u043f\u0443\u0441\u0442\u043e\u0439 \u0432\u0432\u043e\u0434" : "empty input";
        }
        if (symbol.startsWith("'") && symbol.endsWith("'") && symbol.length() >= 2) {
            return (language == Language.RU ? "\u043b\u0438\u0442\u0435\u0440\u0430\u043b " : "literal ") + symbol;
        }
        if (symbol.startsWith("\"") && symbol.endsWith("\"") && symbol.length() >= 2) {
            return (language == Language.RU ? "\u043b\u0438\u0442\u0435\u0440\u0430\u043b " : "literal ") + symbol;
        }
        if (symbol.equals(symbol.toUpperCase(Locale.ROOT))) {
            return (language == Language.RU ? "\u0442\u043e\u043a\u0435\u043d " : "token ") + symbol;
        }
        return (language == Language.RU ? "\u043f\u0440\u0430\u0432\u0438\u043b\u043e " : "rule ") + symbol;
    }

    public String formatConflictCount(int count) {
        if (language == Language.RU) {
            return count + " " + russianConflictWord(count);
        }
        return count + " conflict" + (count == 1 ? "" : "s");
    }

    public String kindLabel(model.Ambiguity.Kind kind) {
        return kind == model.Ambiguity.Kind.DIRECT ? direct() : viaReferences();
    }

    private static String russianConflictWord(int count) {
        int mod10 = count % 10;
        int mod100 = count % 100;
        if (mod10 == 1 && mod100 != 11) {
            return "\u043a\u043e\u043d\u0444\u043b\u0438\u043a\u0442";
        }
        if (mod10 >= 2 && mod10 <= 4 && (mod100 < 10 || mod100 >= 20)) {
            return "\u043a\u043e\u043d\u0444\u043b\u0438\u043a\u0442\u0430";
        }
        return "\u043a\u043e\u043d\u0444\u043b\u0438\u043a\u0442\u043e\u0432";
    }
}
