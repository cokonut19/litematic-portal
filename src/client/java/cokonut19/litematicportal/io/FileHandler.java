package cokonut19.litematicportal.io;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class FileHandler {
    public static List<Path> findLitematic(Path directory, String file_extension) {
        if (directory == null || Files.notExists(directory) || Files.isRegularFile(directory)) {
            throw new IllegalArgumentException("directory not found: " + directory);
        }
        List<Path> result;
        try (Stream<Path> walk = Files.walk(directory, 1)) {
            result = walk.filter(Files::isRegularFile)
                    .filter(p -> p.toString().endsWith(file_extension))
                    .toList();
        }
        catch (IOException e) {
            throw new UncheckedIOException("Failed to list files in " + directory,e);
        }
        return result;
    }
}
