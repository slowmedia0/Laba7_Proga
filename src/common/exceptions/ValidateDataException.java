package common.exceptions;


public class ValidateDataException extends Exception {

    
    public ValidateDataException(String message) {
        super(message);
    }

    
    public ValidateDataException(String message, Throwable cause){
        super(message, cause);
    }

    
    public String generateFullMessage() {
        return generateFullMessage(this);
    }

    
    public static String generateFullMessage(Throwable throwable) {
        if (throwable == null) {
            return " ";
        }

        StringBuilder sb = new StringBuilder();
        Throwable current = throwable;
        boolean isFirst = true;

        while (current != null) {
            String message = current.getMessage();

            if (message != null && !message.trim().isEmpty()) {
                if (!isFirst) {
                    sb.append(" -> ");
                }
                sb.append(message.trim());
                isFirst = false;
            }
            Throwable cause = current.getCause();
            if (cause == current) break;
            current = cause;
        }

        return sb.toString();
    }
}