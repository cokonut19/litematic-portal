package cokonut19.litematicportal.io;

import net.minecraft.util.Util;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

import static cokonut19.litematicportal.util.ClientUtil.*;

public class FileHandler {
    /**
     * Searches for files with the specified file extension in the given directory.
     *
     * @param sourceDir the directory to search for files; must not be {@code null}.
     *                  If the directory does not exist, it is created.
     *                  If the path points to a regular file, a {@code PathIsNotDirectoryException} is returned in the result.
     * @param fileExtension the file extension to filter files by; must not be {@code null}.
     * @return a {@code SearchResult} containing the directory that was searched,
     *         a list of paths matching the file extension, the total number of matching files,
     *         and an optional exception if any error occurred during the operation.
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
     * Imports files from the specified search result into the target directory.
     * The method moves all files returned in the {@code SearchResult.paths()} list to the
     * provided target directory. If the target directory does not exist, it will attempt
     * to create it. If any errors occur during the operation, they will be captured in the
     * result's exception field.
     *
     * @param search the {@code SearchResult} containing the list of files to import; must not be {@code null}.
     * @param targetDir the target directory where the files will be moved; must not be {@code null}.
     *                  If the path does not exist, it will attempt to create it. If it is a regular file,
     *                  an exception will be returned in the result.
     * @return an {@code ImportResult} containing the target directory, counts of successfully
     *         and unsuccessfully imported files, and an optional exception if any error occurred.
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
                getLoggerWithID().error(e.getMessage());
                failed++;
            }
        }
        return new ImportResult(targetDir, successful, failed, Optional.empty());
    }

    public static void moveFilesAsync(Path sourceDir, Path targetDir) {
        var IOThread = Util.ioPool();
        var MainThread = getClient();

        CompletableFuture.supplyAsync(() -> {
            var result = searchFiles(sourceDir, ".litematic");
            if (result.exception().isPresent()) {
                throw new RuntimeException(result.exception().get());
            }
            return result;
            }, IOThread)
                .thenApply(searchResult -> {
                    return importFiles(searchResult, targetDir);
                })
                .thenAcceptAsync(importResult -> printToChat(importResult.toString()),
                        MainThread
                );
    }
}
