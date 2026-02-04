package cokonut19.litematicportal.io;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Objects;

public class FailedToCreateDirectoryException extends UncheckedIOException {
    public FailedToCreateDirectoryException(IOException cause) {
        super(Objects.requireNonNull(cause));
    }
    public FailedToCreateDirectoryException(String message, IOException cause) {
        super(message, Objects.requireNonNull(cause));
    }
}
