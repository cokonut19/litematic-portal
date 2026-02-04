package cokonut19.litematicportal.io;

import net.minecraft.util.Util;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.stream.Stream;

import static cokonut19.litematicportal.util.ClientUtil.*;

public class FileHandler {
    public static SearchResult searchFiles(Path sourceDir, String fileExtension) {
        Objects.requireNonNull(sourceDir, "Source directory cannot be null!");
        Objects.requireNonNull(fileExtension, "File extension cannot be null!"); // Should not happen, not user input

        if (Files.notExists(sourceDir)) {
            try {
                Files.createDirectories(sourceDir);
            } catch (IOException e) {
                throw new FailedToCreateDirectoryException("Source directory could not be created", e);
            }
        } else if (Files.isRegularFile(sourceDir)) {
            throw new PathIsNotDirectoryException("Source path must be a directory");
        }

        List<Path> paths;
        try (Stream<Path> walk = Files.walk(sourceDir, 2)) {
            paths = walk.filter(Files::isRegularFile).filter(p -> p.getFileName().toString().endsWith(fileExtension)).toList();
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to walk file tree", e); //error handled in moveFilesAsync
        }
        return new SearchResult(sourceDir, paths, paths.size());    //file extension is lost but does not matter atm
    }


    public static ImportResult importFiles(SearchResult search, Path targetDir) {
        Objects.requireNonNull(search, "Search result cannot be null!"); // Should not happen, not user input
        Objects.requireNonNull(targetDir, "Target directory cannot be null!");

        if (search.paths().isEmpty()) {
            return new ImportResult(targetDir, 0, 0, search);
        }

        if (Files.notExists(targetDir)) {
            try {
                Files.createDirectories(targetDir);
            } catch (IOException e) {
                throw new FailedToCreateDirectoryException("Target directory could not be created", e);
            }
        } else if (Files.isRegularFile(targetDir)) {
            throw new PathIsNotDirectoryException("Target path must be a directory");
        }

        int successful = 0;
        int failed = 0;
        for (Path p : search.paths()) {
            // Redundant because searchFiles already handles this but still keeping it in for just in case/maintainablity
            if (p == null || Files.isDirectory(p)) {
                failed++;
                continue;
            }
            try {
                Files.move(p, targetDir.resolve(p.getFileName()), StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING); //could apparently fail on some OS
                successful++;
            } catch (IOException e) {
                getLoggerWithID().warn("%s failed. %s".formatted(p, e.getMessage()));
                failed++;
            }
        }
        return new ImportResult(targetDir, successful, failed, search);
    }

    //.thenAcceptAsync(importResult -> printToChat(importResult.toString()), mainThread)
    public static void moveFilesAsync(Path sourceDir, Path targetDir) {
        var ioThread = Util.ioPool();
        var mainThread = getClient();

        CompletableFuture.supplyAsync(() -> searchFiles(sourceDir, ".litematic"), ioThread).thenApply(searchResult -> importFiles(searchResult, targetDir)).whenCompleteAsync((importResult, throwable) -> {
            //Exception often wrapped in CompletionException
            Throwable cause = (throwable instanceof CompletionException) ? throwable.getCause() : throwable;
            switch (cause) {
                case null -> printToChat(importResult.toString());
                case NullPointerException e -> {
                    printToChat(e.getMessage());
                    getLoggerWithID().error(Arrays.toString(e.getStackTrace()), e);
                }
                case FailedToCreateDirectoryException e -> {
                    printToChat(e.getMessage());
                    getLoggerWithID().error(Arrays.toString(e.getStackTrace()), e);
                }
                case PathIsNotDirectoryException e -> {
                    printToChat(e.getMessage());
                    getLoggerWithID().error(Arrays.toString(e.getStackTrace()), e);
                }
                case UncheckedIOException e -> {
                    printToChat(e.getMessage());
                    getLoggerWithID().error(Arrays.toString(e.getStackTrace()), e);
                }
                default -> {
                    printToChat("Unknown error! " + cause.getMessage());
                    getLoggerWithID().error(Arrays.toString(cause.getStackTrace()), cause);
                }
            }
        }, mainThread);
    }
}
