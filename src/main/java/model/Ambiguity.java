package model;

import java.util.List;
import java.util.Objects;

public final class Ambiguity {

    public enum Kind {
        DIRECT,
        EXPANDED
    }

    private final String ruleName;
    private final String firstSymbol;
    private final List<String> alternatives;
    private final Kind kind;

    public Ambiguity(String ruleName, String firstSymbol, List<String> alternatives, Kind kind) {
        this.ruleName = ruleName;
        this.firstSymbol = firstSymbol;
        this.alternatives = List.copyOf(alternatives);
        this.kind = kind;
    }

    public String getRuleName() {
        return ruleName;
    }

    public String getFirstSymbol() {
        return firstSymbol;
    }

    public List<String> getAlternatives() {
        return alternatives;
    }

    public Kind getKind() {
        return kind;
    }

    @Override
    public String toString() {
        return "Ambiguity{kind=" + kind + ", rule='" + ruleName + "', symbol=" + firstSymbol
                + ", alternatives=" + alternatives + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Ambiguity)) {
            return false;
        }
        Ambiguity ambiguity = (Ambiguity) o;
        return kind == ambiguity.kind
                && Objects.equals(ruleName, ambiguity.ruleName)
                && Objects.equals(firstSymbol, ambiguity.firstSymbol)
                && Objects.equals(alternatives, ambiguity.alternatives);
    }

    @Override
    public int hashCode() {
        return Objects.hash(kind, ruleName, firstSymbol, alternatives);
    }
}
