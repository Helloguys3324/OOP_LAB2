package lab1;

import java.util.Stack;

public class ParenthesesValidator {
    private Logger logger;

    public ParenthesesValidator() {
        this.logger = new Logger("Parentheses");
    }

    public ParenthesesValidator(Logger logger) {
        this.logger = logger;
    }

    public boolean isValid(String input) {
        Stack<Integer> openIndices = new Stack<>();

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (c == '(') {
                openIndices.push(i);
                logger.trace("Found '(' at index %", i);
            } else if (c == ')') {
                if (openIndices.isEmpty()) {
                    logger.warn("Closing ')' at index % has no matching '('", i);
                    logger.error("Invalid input \"%\": unmatched ')' at index %", input, i);
                    logger.info("Validation result for \"%\": false", input);
                    return false;
                }
                int openIdx = openIndices.pop();
                logger.info("Matched '(' at index % with ')' at index %", openIdx, i);
            }
        }

        if (!openIndices.isEmpty()) {
            logger.warn("Reached end of input with % unclosed '('", openIndices.size());
            logger.error("Invalid input \"%\": missing closing parenthesis", input);
            logger.info("Validation result for \"%\": false", input);
            return false;
        }

        logger.info("Validation result for \"%\": true", input);
        return true;
    }
}
