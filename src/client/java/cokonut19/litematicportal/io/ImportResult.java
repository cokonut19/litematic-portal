package cokonut19.litematicportal.io;

import java.nio.file.Path;

public record ImportResult(Path directory, int successful, int failed, SearchResult searchResult) {
}
