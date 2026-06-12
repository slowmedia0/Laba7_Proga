package common.utility;

public class ResponseBuilder {

    private static final StringBuilder output = new StringBuilder();


    public static void append(String text) {
        if (text != null && !text.trim().isEmpty()) {
            if (output.length() > 0) {
                output.append("\n");
            }
            output.append(text.trim());
        }
    }

    public static void appendLn(String text) {
        append(text);
        output.append("\n");
    }
    public static void appendError(String text) {
        if (text != null) {
            append("Ошибка: " + text);
        }
    }

    public static void appendSuccess(String text) {
        if (text != null) {
            append("Успешно: " + text);
        }
    }


    public static String getOutput() {
        return output.toString().trim();
    }

    public static void clear() {
        output.delete(0, output.length());
    }
}