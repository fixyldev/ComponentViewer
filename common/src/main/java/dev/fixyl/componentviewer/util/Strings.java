package dev.fixyl.componentviewer.util;

/**
 * Provides utility functions for working
 * with various strings.
 */
public final class Strings {

    private Strings() {}

    /**
     * Escape the given {@link String} to make
     * it JSON-compliant.
     *
     * @param string the string to escape
     * @return the JSON-compliant string equivalent
     */
    public static String escapeJson(String string) {
        StringBuilder stringBuilder = new StringBuilder();

        for (int index = 0; index < string.length(); index++) {
            char ch = string.charAt(index);

            stringBuilder.append(switch (ch) {
                case '"', '\\' -> "\\" + ch;
                case '\b', '\f', '\n', '\r', '\t' -> "\\" + Strings.getBackslashEscape(ch);
                default -> {
                    if (Character.isISOControl(ch)) {
                        yield String.format("\\u%04X", (int) ch);
                    } else {
                        yield ch;
                    }
                }
            });
        }

        return stringBuilder.toString();
    }

    private static char getBackslashEscape(char ch) {
        return switch (ch) {
            case '\b' -> 'b';
            case '\f' -> 'f';
            case '\n' -> 'n';
            case '\r' -> 'r';
            case '\t' -> 't';
            default -> throw new IllegalArgumentException(String.format(
                "The character '\\u%04X' doesn't have a backslash escape!",
                (int) ch
            ));
        };
    }
}
