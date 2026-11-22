package lab2;

import lab1.*;
import java.util.ArrayList;
import java.util.List;

public class Main {
    private static DisplayManager findManager(List<DisplayManager> managers, String name) {
        for (DisplayManager m : managers) {
            if (m.getName().equals(name)) {
                return m;
            }
        }
        return null;
    }

    private static Display findDisplay(List<Display> displays, String model) {
        for (Display d : displays) {
            if (d.getModel().equals(model)) {
                return d;
            }
        }
        return null;
    }

    // Commands are intentionally CASE-SENSITIVE (must be UPPERCASE like ASSIST, BUY, etc.)
    private static void parseCommand(
        String line,
        int lineNum,
        List<DisplayManager> managers,
        List<Display> displays,
        Logger parserLog,
        Logger managerLog,
        Logger displayLog
    ) {
        String cleanLine = line.trim();
        if (cleanLine.isEmpty() || cleanLine.startsWith("#")) {
            return;
        }

        String[] parts = cleanLine.split("\\s+");
        String cmd = parts[0];

        if (cmd.equals("MANAGER")) {
            if (parts.length != 2) {
                parserLog.error("Line %: MANAGER expects 1 argument (<name>), got %", lineNum, parts.length - 1);
                return;
            }
            String name = parts[1];
            if (findManager(managers, name) != null) {
                parserLog.warn("Line %: Manager '%' already exists - skipping duplicate", lineNum, name);
                return;
            }
            managers.add(new DisplayManager(name, managerLog));

        } else if (cmd.equals("DISPLAY")) {
            if (parts.length != 5) {
                parserLog.error(
                    "Line %: DISPLAY expects 4 arguments (<model> <w> <h> <ppi>), got %",
                    lineNum,
                    parts.length - 1
                );
                return;
            }
            String model = parts[1];
            if (findDisplay(displays, model) != null) {
                parserLog.warn("Line %: Display '%' already exists - skipping duplicate", lineNum, model);
                return;
            }
            try {
                int w = Integer.parseInt(parts[2]);
                int h = Integer.parseInt(parts[3]);
                float ppi = Float.parseFloat(parts[4]);
                if (w <= 0 || h <= 0 || ppi <= 0) {
                    parserLog.error("Line %: Display dimensions and PPI must be positive numbers", lineNum);
                    return;
                }
                displays.add(new Display(model, w, h, ppi, displayLog));
            } catch (NumberFormatException e) {
                parserLog.error("Line %: Invalid number in DISPLAY command: '%'", lineNum, cleanLine);
            }

        } else if (cmd.equals("ASSIGN")) {
            if (parts.length != 3) {
                parserLog.error("Line %: ASSIGN expects 2 arguments (<model> <manager>), got %", lineNum, parts.length - 1);
                return;
            }
            Display disp = findDisplay(displays, parts[1]);
            if (disp == null) {
                parserLog.error("Line %: Display '%' does not exist", lineNum, parts[1]);
                return;
            }
            DisplayManager mgr = findManager(managers, parts[2]);
            if (mgr == null) {
                parserLog.error("Line %: Manager '%' does not exist", lineNum, parts[2]);
                return;
            }
            mgr.assignDisplay(disp);

        } else if (cmd.equals("COMPARE")) {
            if (parts.length != 3) {
                parserLog.error("Line %: COMPARE expects 2 arguments (<model1> <model2>), got %", lineNum, parts.length - 1);
                return;
            }
            Display first = findDisplay(displays, parts[1]);
            if (first == null) {
                parserLog.error("Line %: Display '%' does not exist", lineNum, parts[1]);
                return;
            }
            Display second = findDisplay(displays, parts[2]);
            if (second == null) {
                parserLog.error("Line %: Display '%' does not exist", lineNum, parts[2]);
                return;
            }
            first.compareWithMonitor(second);

        } else if (cmd.equals("ASSIST")) {
            if (parts.length != 2) {
                parserLog.error("Line %: ASSIST expects 1 argument (<manager>), got %", lineNum, parts.length - 1);
                return;
            }
            DisplayManager mgr = findManager(managers, parts[1]);
            if (mgr == null) {
                parserLog.error("Line %: Manager '%' does not exist", lineNum, parts[1]);
                return;
            }
            mgr.assist();

        } else if (cmd.equals("BUY")) {
            if (parts.length != 3) {
                parserLog.error("Line %: BUY expects 2 arguments (<model> <manager>), got %", lineNum, parts.length - 1);
                return;
            }
            Display disp = findDisplay(displays, parts[1]);
            if (disp == null) {
                parserLog.error("Line %: Display '%' does not exist", lineNum, parts[1]);
                return;
            }
            DisplayManager mgr = findManager(managers, parts[2]);
            if (mgr == null) {
                parserLog.error("Line %: Manager '%' does not exist", lineNum, parts[2]);
                return;
            }
            Display bought = mgr.buyDisplay(disp);
            if (bought == null) {
                parserLog.error("Line %: Display '%' is not assigned to manager '%'", lineNum, parts[1], parts[2]);
            }

        } else {
            parserLog.error("Line %: Unknown command '%' (commands are case-sensitive UPPERCASE)", lineNum, cmd);
        }
    }

    private static void runCommandFile(String filePath, Formatter sharedFormatter, ConsoleSink sharedSink) {
        // Aggregation: all shop loggers share ONE Formatter and ONE ConsoleSink created in main
        Logger readerLogger = new Logger("Reader", sharedFormatter, sharedSink);
        Logger parserLogger = new Logger("Parser", sharedFormatter, sharedSink);
        Logger managerLogger = new Logger("Manager", sharedFormatter, sharedSink);
        Logger displayLogger = new Logger("Display", sharedFormatter, sharedSink);

        CommandReader reader = new CommandReader(readerLogger);
        List<String> lines = reader.readLines(filePath);
        if (lines.isEmpty()) {
            return;
        }

        List<DisplayManager> managers = new ArrayList<>();
        List<Display> displays = new ArrayList<>();

        for (int i = 0; i < lines.size(); i++) {
            parseCommand(lines.get(i), i + 1, managers, displays, parserLogger, managerLogger, displayLogger);
        }
    }

    public static void main(String[] args) {
        // Shared Formatter and ConsoleSink (Aggregation demo)
        Formatter sharedFormatter = new Formatter("[%t] [%l] %n: %m");
        ConsoleSink sharedSink = new ConsoleSink();

        // If a command file path is passed via CLI arguments, run just that file
        if (args.length > 0) {
            Logger.setLevel(LogLevel.TRACE);
            runCommandFile(args[0], sharedFormatter, sharedSink);
            return;
        }

        // --- PART A: Formatter edge cases, Composition vs Aggregation, and Lab 1 Validator ---
        Logger.setLevel(LogLevel.TRACE);
        Logger demoLogger = new Logger("Demo"); // Composition constructor
        demoLogger.info("--- Formatter & varargs tests ---");
        demoLogger.info("Ratio is 100%n");
        demoLogger.info("100%% test: found pair at % and %", 3, 7);

        Logger specialLogger = new Logger("Special", new Formatter("%l >> %n: %m (%r)"), sharedSink);
        specialLogger.info("Custom format test with shared sink");

        demoLogger.info("--- ParenthesesValidator (Lab 1) ---");
        ParenthesesValidator validator = new ParenthesesValidator(
            new Logger("Parentheses", sharedFormatter, sharedSink)
        );
        String[] parenTests = {"()", ")(()))", "(", "(())((()())())"};
        for (String test : parenTests) {
            validator.isValid(test);
        }

        demoLogger.info("--- shop-monday.txt (TRACE) ---");
        Logger.setLevel(LogLevel.TRACE);
        sharedFormatter.setFormat("[%l] %n: %m");
        runCommandFile("data/shop-monday.txt", sharedFormatter, sharedSink);

        demoLogger.info("--- shop-tuesday.txt (TRACE) ---");
        runCommandFile("data/shop-tuesday.txt", sharedFormatter, sharedSink);

        demoLogger.info("--- shop-wednesday.txt (INFO) ---");
        Logger.setLevel(LogLevel.INFO);
        runCommandFile("data/shop-wednesday.txt", sharedFormatter, sharedSink);

        demoLogger.info("--- Missing file test (ERROR) ---");
        Logger.setLevel(LogLevel.ERROR);
        runCommandFile("data/does-not-exist.txt", sharedFormatter, sharedSink);
    }
}
