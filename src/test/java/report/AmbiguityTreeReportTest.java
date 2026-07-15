package report;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import finder.AmbiguityFinder;
import model.Ambiguity;
import reader.TestUtils;
import report.AmbiguityTreeLabels.Language;
import report.AmbiguityTreeReport.ReportContext;

public class AmbiguityTreeReportTest {

    @Test
    void rendersTreeGroupedByRuleInRussian() {
        Map<String, model.Rule> rules = TestUtils.getRules(TestUtils.JSON);
        List<Ambiguity> ambiguities = AmbiguityFinder.findDirect(rules);
        ReportContext context = ReportContext.of("JSON.g4", rules.size(), ambiguities, ambiguities.size(), Language.RU);

        String report = AmbiguityTreeReport.format(ambiguities, context, 80);

        assertTrue(report.contains("\u043f\u0440\u0430\u0432\u0438\u043b\u043e obj"));
        assertTrue(report.contains("\u043f\u0440\u0430\u0432\u0438\u043b\u043e arr"));
        assertTrue(report.contains("\u043e\u0431\u0449\u0438\u0439 \u043f\u0440\u0435\u0444\u0438\u043a\u0441"));
        assertTrue(report.contains("\u043b\u0438\u0442\u0435\u0440\u0430\u043b '{'"));
        assertTrue(report.contains("\u0432\u0430\u0440\u0438\u0430\u043d\u0442 1:"));
        assertTrue(report.contains("[\u043f\u0440\u044f\u043c\u0430\u044f]"));
        assertTrue(report.contains("+--"));
        assertTrue(report.contains("`--"));
    }

    @Test
    void rendersEmptyReport() {
        ReportContext context = ReportContext.of("empty.g4", 0, List.of(), 0, Language.EN);
        String report = AmbiguityTreeReport.format(List.of(), context);

        assertTrue(report.contains("No ambiguities found"));
    }
}
