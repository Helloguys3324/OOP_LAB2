package lab1;

public class Logger {
    private static LogLevel level = LogLevel.TRACE;

    private String name;
    private Formatter formatter;
    private ConsoleSink sink;

    // composition
    public Logger(String name) {
        this.name = name;
        this.formatter = new Formatter();
        this.sink = new ConsoleSink();
    }

    // aggregation
    public Logger(String name, Formatter formatter, ConsoleSink sink) {
        this.name = name;
        this.formatter = formatter;
        this.sink = sink;
    }

    public static void setLevel(LogLevel newLevel) {
        level = newLevel;
    }

    public static LogLevel getLevel() {
        return level;
    }

    public void log(LogLevel lvl, String msg) {
        if (lvl.ordinal() < level.ordinal()) {
            return;
        }
        String line = formatter.format(lvl, name, msg);
        sink.write(line);
    }

    public void log(LogLevel lvl, String tpl, Object... args) {
        if (lvl.ordinal() < level.ordinal()) {
            return;
        }
        String filled = formatter.formatMessage(tpl, args);
        String line = formatter.format(lvl, name, filled);
        sink.write(line);
    }

    public void trace(String message) { log(LogLevel.TRACE, message); }
    public void trace(String template, Object... args) { log(LogLevel.TRACE, template, args); }

    public void info(String message) { log(LogLevel.INFO, message); }
    public void info(String template, Object... args) { log(LogLevel.INFO, template, args); }

    public void warn(String message) { log(LogLevel.WARN, message); }
    public void warn(String template, Object... args) { log(LogLevel.WARN, template, args); }

    public void error(String message) { log(LogLevel.ERROR, message); }
    public void error(String template, Object... args) { log(LogLevel.ERROR, template, args); }
}
