package reader;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public final class G4Reader {

    private G4Reader() {
    }

    public static String readParser(String path) {
        if (!path.endsWith(".g4")) {
            throw new IllegalArgumentException("path do not end by .g4. path " + path);
        }
        try {
            return Files.readString(Path.of(path), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalArgumentException("Cannot read grammar file: " + path, e);
        }
    }
}
