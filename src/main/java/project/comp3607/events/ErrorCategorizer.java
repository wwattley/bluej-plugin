package project.comp3607.events;

import java.util.Locale;
import java.util.regex.Pattern;

public class ErrorCategorizer {
    private static final Pattern WHITESPACE = Pattern.compile("\\s+");

    public ErrorCategoryEnum categorize(String message) {
        if (message == null || message.isBlank()) {
            return ErrorCategoryEnum.OTHER;
        }


        String normalized = WHITESPACE.matcher(message)
            .replaceAll(" ")
            .trim()
            .toLowerCase(Locale.ROOT);

        if (normalized.contains("missing return statement")) {
            return ErrorCategoryEnum.MISSING_RETURN;
        }

        if (normalized.contains("reached end of file while parsing")) {
            return ErrorCategoryEnum.BRACES;
        }

        if (normalized.contains("cannot find symbols")
            || normalized.contains("cannot resolve")
            || normalized.contains("package") && normalized.contains("does not exist")) {
                return ErrorCategoryEnum.SYMBOL;
            }

        if (normalized.contains("incompatible types")
            || normalized.contains("incompatible operand")
            || normalized.contains("cannot be applied")
            || normalized.contains("no suitable method")
            || normalized.contains("bad operand types")
            || normalized.contains("unreported exception")
            || normalized.contains("possible lossy conversion")
            || normalized.contains("cannot be converted")) {
            return ErrorCategoryEnum.TYPE;
        }

        if (normalized.contains("expected")
            || normalized.contains("illegal start")
            || normalized.contains("not a statement")
            || normalized.contains("malformed")) {
            return ErrorCategoryEnum.SYNTAX;
        }

        return ErrorCategoryEnum.OTHER;
    }
}
