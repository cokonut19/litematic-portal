package cokonut19.litematicportal.io;

public class PathIsNotDirectoryException extends IllegalArgumentException {
    public PathIsNotDirectoryException() {
        super();
    }
    public PathIsNotDirectoryException(String message) {
        super(message);
    }
    public PathIsNotDirectoryException(Throwable cause) {
        super(cause);
    }
    public PathIsNotDirectoryException(String message, Throwable cause) {
        super(message, cause);
    }
}
