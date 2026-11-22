package lab2;

import lab1.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class CommandReader {
    private final Logger logger;

    public CommandReader(Logger logger) {
        this.logger = logger;
    }

    public List<String> readLines(String filePath) {
        Path path = Path.of(filePath);
        if (!Files.exists(path)) {
            logger.error("Command file not found: %", filePath);
            return new ArrayList<>();
        }

        try {
            List<String> lines = Files.readAllLines(path);
            logger.trace("Loaded % lines from %", lines.size(), filePath);
            return lines;
        } catch (IOException e) {
            logger.error("Failed to read file %: %", filePath, e.getMessage());
            return new ArrayList<>();
        }
    }
}
