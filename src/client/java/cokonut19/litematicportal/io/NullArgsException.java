package cokonut19.litematicportal.io;

public class NullArgsException extends IllegalArgumentException {
    public NullArgsException() {
        super();
    }
    public NullArgsException(String message) {
        super(message);
    }
    public NullArgsException(Throwable cause) {
        super(cause);
    }
    public NullArgsException(String message, Throwable cause) {
        super(message, cause);
    }
}
