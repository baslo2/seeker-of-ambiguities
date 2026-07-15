package report;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import model.Ambiguity;
import model.Ambiguity.Kind;
import report.AmbiguityTreeLabels.Language;

public final class AmbiguityTreeReport {

    private static final int DEFAULT_MAX_LENGTH = 100;

    private AmbiguityTreeReport() {
    }

    public static String format(List<Ambiguity> ambiguities, ReportContext context) {
        return format(ambiguities, context, DEFAULT_MAX_LENGTH);
    }

    public static String format(List<Ambiguity> ambiguities, ReportContext context, int maxAlternativeLength) {
        AmbiguityTreeLabels labels = AmbiguityTreeLabels.of(context.language());
        StringBuilder report = new StringBuilder();
        appendHeader(report, labels, context, ambiguities);

        if (ambiguities.isEmpty()) {
            report.append("  ").append(labels.noAmbiguities()).append(System.lineSeparator());
            return report.toString();
        }

        Map<String, List<Ambiguity>> byRule = ambiguities.stream()
                .collect(Collectors.groupingBy(Ambiguity::getRuleName, LinkedHashMap::new, Collectors.toList()));

        List<String> ruleNames = new ArrayList<>(byRule.keySet());
        ruleNames.sort(Comparator.naturalOrder());

        for (int ruleIndex = 0; ruleIndex < ruleNames.size(); ruleIndex++) {
            String ruleName = ruleNames.get(ruleIndex);
            List<Ambiguity> ruleAmbiguities = byRule.get(ruleName).stream()
                    .sorted(Comparator.comparing(Ambiguity::getFirstSymbol).thenComparing(Ambiguity::getKind))
                    .toList();
            boolean lastRule = ruleIndex == ruleNames.size() - 1;
            appendRule(report, labels, ruleName, ruleAmbiguities, maxAlternativeLength, lastRule);
        }

        return report.toString();
    }

    private static void appendHeader(StringBuilder report, AmbiguityTreeLabels labels, ReportContext context,
            List<Ambiguity> ambiguities) {
        long directCount = ambiguities.stream().filter(ambiguity -> ambiguity.getKind() == Kind.DIRECT).count();
        long expandedCount = ambiguities.stream().filter(ambiguity -> ambiguity.getKind() == Kind.EXPANDED).count();
        long affectedRules = ambiguities.stream().map(Ambiguity::getRuleName).distinct().count();

        report.append("==============================================================").append(System.lineSeparator());
        report.append("  ").append(labels.title()).append(System.lineSeparator());
        report.append("==============================================================").append(System.lineSeparator());
        report.append("  ").append(labels.grammar()).append(":  ").append(context.grammarPath()).append(System.lineSeparator());
        report.append("  ").append(labels.rules()).append(":    ").append(context.rulesCount()).append(System.lineSeparator());
        report.append("  ").append(labels.found()).append(":    ").append(labels.formatConflictCount(ambiguities.size()))
                .append("  [").append(labels.directCount()).append(": ").append(directCount)
                .append(", ").append(labels.viaRuleRefsCount()).append(": ").append(expandedCount)
                .append(", ").append(labels.affectedRules()).append(": ").append(affectedRules).append(']')
                .append(System.lineSeparator());

        if (context.limitApplied()) {
            report.append("  ").append(labels.showing()).append(":  ")
                    .append(labels.firstWord()).append(' ').append(context.shownCount()).append(' ')
                    .append(labels.firstOf()).append(' ').append(context.totalCount())
                    .append(System.lineSeparator());
        }

        report.append(System.lineSeparator());
    }

    private static void appendRule(StringBuilder report, AmbiguityTreeLabels labels, String ruleName,
            List<Ambiguity> ambiguities, int maxAlternativeLength, boolean lastRule) {
        report.append(lastRule ? "`-- " : "+-- ")
                .append(labels.rule()).append(' ').append(ruleName)
                .append("  (").append(labels.formatConflictCount(ambiguities.size())).append(')')
                .append(System.lineSeparator());

        for (int conflictIndex = 0; conflictIndex < ambiguities.size(); conflictIndex++) {
            Ambiguity ambiguity = ambiguities.get(conflictIndex);
            boolean lastConflict = conflictIndex == ambiguities.size() - 1;
            String rulePrefix = lastRule ? "    " : "|   ";
            String conflictPrefix = rulePrefix + (lastConflict ? "`-- " : "+-- ");
            String detailPrefix = rulePrefix + (lastConflict ? "    " : "|   ");

            report.append(conflictPrefix)
                    .append(labels.sharedPrefix()).append(": ").append(labels.humanizeSymbol(ambiguity.getFirstSymbol()))
                    .append("  [").append(labels.kindLabel(ambiguity.getKind())).append(']')
                    .append(System.lineSeparator());

            List<String> alternatives = ambiguity.getAlternatives();
            for (int altIndex = 0; altIndex < alternatives.size(); altIndex++) {
                boolean lastAlt = altIndex == alternatives.size() - 1;
                String branch = detailPrefix + (lastAlt ? "`-- " : "+-- ");
                report.append(branch)
                        .append(labels.alternative()).append(' ').append(altIndex + 1).append(": ")
                        .append(AmbiguityFormatter.compact(alternatives.get(altIndex), maxAlternativeLength))
                        .append(System.lineSeparator());
            }

            if (!lastConflict) {
                report.append(detailPrefix).append(System.lineSeparator());
            }
        }

        if (!lastRule) {
            report.append("|").append(System.lineSeparator());
        }
    }

    public record ReportContext(String grammarPath, int rulesCount, int shownCount, int totalCount, Language language) {

        public ReportContext(String grammarPath, int rulesCount, int shownCount, int totalCount) {
            this(grammarPath, rulesCount, shownCount, totalCount, Language.RU);
        }

        public boolean limitApplied() {
            return totalCount > shownCount;
        }

        public static ReportContext of(String grammarPath, int rulesCount, List<Ambiguity> shown, int totalCount,
                Language language) {
            return new ReportContext(grammarPath, rulesCount, shown.size(), totalCount, language);
        }
    }
}
