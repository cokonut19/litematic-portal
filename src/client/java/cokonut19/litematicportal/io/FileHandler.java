package cokonut19.litematicportal.io;

import net.minecraft.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Executor;
import java.util.stream.Stream;

public class FileHandler {
    private static final String MOD_ID = "litematic-portal";
    private static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    /**
     * Searches for files with the specified file extension in the given directory.
     * If the directory does not exist, it attempts to create it. The method only
     * searches at the top level of the directory (non-recursively).
     *
     * @param sourceDir the directory to search for files; must not be {@code null}.
     *                  If the directory does not exist, it will be created.
     * @param fileExtension the file extension to filter files; must not be {@code null}.
     *                      The method matches files whose names end with this string.
     * @return a {@code SearchResult} containing the directory searched, the list of paths
     *         of matching files, and the count of matching files.
     * @throws NullPointerException if {@code sourceDir} or {@code fileExtension} is {@code null}.
     * @throws IllegalArgumentException if {@code sourceDir} points to a regular file
     *                                  instead of a directory.
     * @throws UncheckedIOException if an I/O error occurs while creating the directory
     *                               or reading files from the directory.
     */
    public static SearchResult searchFiles(Path sourceDir, String fileExtension) {
        if (sourceDir == null || fileExtension == null) {
            return new SearchResult(null, null, 0, Optional.of(new NullArgsException()));
        }

        if (Files.notExists(sourceDir)) {
            try {
                Files.createDirectories(sourceDir);
            } catch (IOException e) {
                return new SearchResult(sourceDir,List.of(), 0, Optional.of(new FailedToCreateDirectoryException(e)));
            }
        }
        else if (Files.isRegularFile(sourceDir)) {
            return new SearchResult(sourceDir,List.of(), 0, Optional.of(new PathIsNotDirectoryException()));
        }

        List<Path> paths;
        try (Stream<Path> walk = Files.walk(sourceDir, 1)) {
            paths = walk.filter(Files::isRegularFile)
                    .filter(p -> p.getFileName().toString().endsWith(fileExtension))
                    .toList();
        }
        catch (IOException e) {
            return new SearchResult(sourceDir, List.of(), 0,  Optional.of(e));
        }
        return new SearchResult(sourceDir, paths, paths.size(), Optional.empty());    //file extension is lost but does not matter atm
    }

    /**
     * Moves the files listed in the provided {@code SearchResult} to the specified target directory.
     * If the target directory does not exist, it is created. Files in the target directory
     * with the same name are replaced.
     *
     * @param search the search result containing the files to move; must not be {@code null}.
     * @param targetDir the directory to move the files to; must not be {@code null}.
     *                  It must represent a directory and not a file.
     * @return an {@code ImportResult} containing the directory where files were moved, the count
     *         of successfully moved files, and the count of files that failed to move.
     * @throws NullPointerException if {@code search} or {@code targetDir} is {@code null}.
     * @throws IllegalArgumentException if {@code targetDir} points to a file instead of a directory.
     * @throws UncheckedIOException if an I/O error occurs while creating the target directory
     *                               or moving files.
     */
    public static ImportResult importFiles(SearchResult search, Path targetDir) {
        if (search == null || targetDir == null) {
            return new ImportResult(null, 0,0, Optional.of(new NullArgsException()));
        }

        if (search.paths().isEmpty()) {
            return new ImportResult(targetDir, 0, 0, Optional.empty());
        }

        if (Files.notExists(targetDir)) {
            try {
                Files.createDirectories(targetDir);
            } catch (IOException e) {
                return new ImportResult(targetDir, 0,0, Optional.of(new FailedToCreateDirectoryException(e)));
            }
        }
        else if (Files.isRegularFile(targetDir)) {
            return new ImportResult(targetDir, 0, 0, Optional.of(new PathIsNotDirectoryException()));
        }

        int successful = 0;
        int failed = 0;
        for(Path p: search.paths()) {
            if (p == null || Files.isDirectory(p)) {
                failed++;
                continue;
            }
            try {
                Files.move(p, targetDir.resolve(p.getFileName()), StandardCopyOption.REPLACE_EXISTING);
                successful++;
            } catch (IOException e) {
                LOGGER.error(e.getMessage());
                failed++;
            }
        }
        return new ImportResult(targetDir, successful, failed, Optional.empty());
    }

    public static void moveFilesAsync(Path sourceDir, Path targetDir) {
        var IOExecutor = Util.ioPool();

        IOExecutor.execute(() -> {
                var searchResult = searchFiles(sourceDir, ".litematic");
        });
    }
}
