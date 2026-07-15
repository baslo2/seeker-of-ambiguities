package reader;

import java.io.File;
import java.util.Map;

import model.Rule;
import parser.RuleParser;

public class TestUtils {

    public final static String SQL = "/SQLParser.g4";
    public final static String JSON = "/JSON.g4";
    public final static String RESOUCE = new File("src/test/resources").getAbsolutePath();

    public static Map<String, Rule> getRules(String parser) {
        return RuleParser.parse(G4Reader.readParser(TestUtils.RESOUCE + parser));
    }

}
