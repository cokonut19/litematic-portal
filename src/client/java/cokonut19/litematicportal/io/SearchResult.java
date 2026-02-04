package cokonut19.litematicportal.io;

import java.nio.file.Path;
import java.util.List;

public record SearchResult(Path directory, List<Path> paths, int amount) {
}
