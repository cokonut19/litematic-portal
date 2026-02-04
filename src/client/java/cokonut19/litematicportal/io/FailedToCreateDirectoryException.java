package cokonut19.litematicportal.io;

import java.io.IOException;

public class FailedToCreateDirectoryException extends IOException {
    public FailedToCreateDirectoryException() {
        super();
    }
    public FailedToCreateDirectoryException(String message) {
        super(message);
    }
    public FailedToCreateDirectoryException(Throwable cause) {
        super(cause);
    }
    public FailedToCreateDirectoryException(String message, Throwable cause) {
        super(message, cause);
    }
}
