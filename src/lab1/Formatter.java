package lab1;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

public class Formatter {
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm:ss");
    private static final Random RANDOM = new Random();

    private static final String[][] RANDOM_PHRASES = {
        {"Inspecting step", "Scanning item", "Checking state"},
        {"Operation completed", "Everything looks good", "All clear"},
        {"Suspicious situation", "Heads up", "Worth checking"},
        {"Operation failed", "Invalid input", "Something went wrong"}
    };

    private String format;

    public Formatter() {
        this.format = "[%t] [%l] %n: %m";
    }

    public Formatter(String format) {
        this.format = format;
    }

    public void setFormat(String newFormat) {
        this.format = newFormat;
    }

    public String getFormat() {
        return format;
    }

    public String formatMessage(String tpl, Object... args) {
        if (args == null || args.length == 0) {
            return tpl;
        }

        StringBuilder text = new StringBuilder();
        int idx = 0;

        for (int i = 0; i < tpl.length(); i++) {
            char c = tpl.charAt(i);
            if (c == '%') {
                if (i + 1 < tpl.length() && tpl.charAt(i + 1) == '%') {
                    text.append('%');
                    i++;
                } else if (idx < args.length) {
                    text.append(args[idx]);
                    idx++;
                } else {
                    text.append('%');
                }
            } else {
                text.append(c);
            }
        }
        return text.toString();
    }

    // single pass so %n inside message doesn't get replaced
    public String format(LogLevel lvl, String logName, String msg) {
        StringBuilder res = new StringBuilder();
        String now = LocalTime.now().format(TIME_FMT);

        for (int i = 0; i < format.length(); i++) {
            char c = format.charAt(i);
            if (c == '%' && i + 1 < format.length()) {
                char next = format.charAt(i + 1);
                switch (next) {
                    case 't': res.append(now); i++; break;
                    case 'l': res.append(lvl.name()); i++; break;
                    case 'n': res.append(logName); i++; break;
                    case 'm': res.append(msg); i++; break;
                    case 'r': res.append(getRandomPhrase(lvl)); i++; break;
                    default: res.append(c); break;
                }
            } else {
                res.append(c);
            }
        }
        return res.toString();
    }

    private String getRandomPhrase(LogLevel level) {
        String[] options = RANDOM_PHRASES[level.ordinal()];
        return options[RANDOM.nextInt(options.length)];
    }
}
