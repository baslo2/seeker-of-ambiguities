package report;

import java.util.List;
import java.util.stream.Collectors;

import model.Ambiguity;

public final class AmbiguityFormatter {

    private static final int DEFAULT_MAX_LENGTH = 100;

    private AmbiguityFormatter() {
    }

    public static String formatAmbiguity(Ambiguity ambiguity) {
        return formatAmbiguity(ambiguity, DEFAULT_MAX_LENGTH);
    }

    public static String formatAmbiguity(Ambiguity ambiguity, int maxAlternativeLength) {
        String alternatives = ambiguity.getAlternatives().stream()
                .map(alternative -> compact(alternative, maxAlternativeLength))
                .collect(Collectors.joining(" | "));
        return "- [" + ambiguity.getKind() + "] rule '" + ambiguity.getRuleName() + "' shares prefix "
                + ambiguity.getFirstSymbol() + ": " + alternatives;
    }

    public static String compact(String alternative) {
        return compact(alternative, DEFAULT_MAX_LENGTH);
    }

    public static String compact(String alternative, int maxLength) {
        String oneLine = alternative.replaceAll("\\s+", " ").strip();
        if (oneLine.length() <= maxLength) {
            return oneLine;
        }
        return oneLine.substring(0, maxLength - 3) + "...";
    }
}
