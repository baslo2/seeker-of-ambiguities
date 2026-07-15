package reader;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class G4ReaderTest {

    @Test
    void readParserTestThrowError() {
        assertThrows(IllegalArgumentException.class,
                () -> G4Reader.readParser(TestUtils.RESOUCE + TestUtils.SQL.replace(".g4", ".mp4")));
    }

    @Test
    void readParserTest() {
        assertEquals(G4Reader.readParser(TestUtils.RESOUCE + TestUtils.SQL).isEmpty(), false);
    }
}