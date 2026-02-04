package cokonut19.litematicportal.io;

import java.nio.file.Path;
import java.util.Optional;

public record ImportResult(Path directory, int successful, int failed, Optional<Exception> exception) {
}
