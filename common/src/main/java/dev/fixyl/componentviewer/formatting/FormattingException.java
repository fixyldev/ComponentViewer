package dev.fixyl.componentviewer.formatting;

public class FormattingException extends RuntimeException {

    public FormattingException() {
        super();
    }

    public FormattingException(String message) {
        super(message);
    }

    public FormattingException(Throwable cause) {
        super(cause);
    }

    public FormattingException(String message, Throwable cause) {
        super(message, cause);
    }
}
