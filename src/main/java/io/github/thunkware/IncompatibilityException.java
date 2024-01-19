package io.github.thunkware;

/**
 * Exception thrown to indicate incompatibility
 */
public class IncompatibilityException extends RuntimeException {

    private static final long serialVersionUID = -814227121570861788L;

    public IncompatibilityException() {
        super();
    }

    public IncompatibilityException(String message) {
        super(message);
    }

    public IncompatibilityException(String message, Throwable cause) {
        super(message, cause);
    }

    public IncompatibilityException(Throwable cause) {
        super(cause);
    }
}
