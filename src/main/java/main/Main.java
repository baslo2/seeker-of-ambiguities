package main;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.io.File;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import finder.AmbiguityFinder;
import model.Ambiguity;
import model.Ambiguity.Kind;
import model.Rule;
import parser.RuleParser;
import reader.G4Reader;
import report.AmbiguityFormatter;
import report.AmbiguityTreeLabels.Language;
import report.AmbiguityTreeReport;
import report.AmbiguityTreeReport.ReportContext;

public class Main {

    private static final String DEFAULT_PATH = new File("src/test/resources/JSON.g4").getAbsolutePath();

    public static void main(String[] args) {
        Config config = Config.parse(args);
        String parserRules = G4Reader.readParser(config.grammarPath());
        Map<String, Rule> rules = RuleParser.parse(parserRules);
        List<Ambiguity> ambiguities = loadAmbiguities(rules, config.kindFilter());
        ambiguities = sortAmbiguities(ambiguities);

        int totalCount = ambiguities.size();
        List<Ambiguity> toPrint = config.limit() < 0 ? ambiguities : ambiguities.stream()
                .limit(config.limit())
                .toList();

        if (config.outputFormat() == OutputFormat.TREE) {
            ReportContext context = ReportContext.of(
                    config.grammarPath(), rules.size(), toPrint, totalCount, config.language());
            printUtf8(AmbiguityTreeReport.format(toPrint, context));
            return;
        }

        printFlatReport(config.grammarPath(), rules.size(), toPrint, totalCount);
    }

    private static void printUtf8(String text) {
        PrintStream out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        out.print(text);
    }

    private static void printFlatReport(String grammarPath, int rulesCount, List<Ambiguity> toPrint, int totalCount) {
        long directCount = toPrint.stream().filter(ambiguity -> ambiguity.getKind() == Kind.DIRECT).count();
        long expandedCount = toPrint.stream().filter(ambiguity -> ambiguity.getKind() == Kind.EXPANDED).count();
        long affectedRules = toPrint.stream().map(Ambiguity::getRuleName).distinct().count();

        System.out.println("Grammar file: " + grammarPath);
        System.out.println("Rules parsed: " + rulesCount);
        System.out.println("Ambiguities found: " + toPrint.size()
                + " (direct: " + directCount + ", expanded: " + expandedCount + ", rules: " + affectedRules + ")");
        if (totalCount > toPrint.size()) {
            System.out.println("Showing first " + toPrint.size() + " of " + totalCount + " ambiguities.");
        }
        toPrint.forEach(ambiguity -> System.out.println(AmbiguityFormatter.formatAmbiguity(ambiguity)));
    }

    private static List<Ambiguity> loadAmbiguities(Map<String, Rule> rules, KindFilter kindFilter) {
        return switch (kindFilter) {
            case ALL -> AmbiguityFinder.find(rules);
            case DIRECT -> AmbiguityFinder.findDirect(rules);
            case EXPANDED -> AmbiguityFinder.findExpanded(rules);
        };
    }

    private static List<Ambiguity> sortAmbiguities(List<Ambiguity> ambiguities) {
        return ambiguities.stream()
                .sorted(Comparator.comparing(Ambiguity::getRuleName).thenComparing(Ambiguity::getFirstSymbol))
                .toList();
    }

    private enum KindFilter {
        ALL,
        DIRECT,
        EXPANDED
    }

    private enum OutputFormat {
        TREE,
        FLAT
    }

    private record Config(String grammarPath, KindFilter kindFilter, int limit, OutputFormat outputFormat,
            Language language) {

        static Config parse(String[] args) {
            String grammarPath = DEFAULT_PATH;
            KindFilter kindFilter = KindFilter.ALL;
            int limit = -1;
            OutputFormat outputFormat = OutputFormat.TREE;
            Language language = Language.RU;

            for (String arg : args) {
                if (arg.equals("--direct-only")) {
                    kindFilter = KindFilter.DIRECT;
                } else if (arg.equals("--expanded-only")) {
                    kindFilter = KindFilter.EXPANDED;
                } else if (arg.equals("--flat")) {
                    outputFormat = OutputFormat.FLAT;
                } else if (arg.equals("--tree")) {
                    outputFormat = OutputFormat.TREE;
                } else if (arg.startsWith("--limit=")) {
                    limit = Integer.parseInt(arg.substring("--limit=".length()));
                } else if (arg.startsWith("--format=")) {
                    outputFormat = OutputFormat.valueOf(arg.substring("--format=".length()).toUpperCase());
                } else if (arg.startsWith("--lang=")) {
                    language = Language.valueOf(arg.substring("--lang=".length()).toUpperCase());
                } else if (arg.endsWith(".g4")) {
                    grammarPath = arg;
                }
            }
            return new Config(grammarPath, kindFilter, limit, outputFormat, language);
        }
    }
}
