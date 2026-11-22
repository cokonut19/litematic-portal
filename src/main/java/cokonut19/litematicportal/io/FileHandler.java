package cokonut19.litematicportal.io;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;

public final class FileHandler {
	public static final String EXTENSION = ".litematic";

	private static final Logger LOGGER = LoggerFactory.getLogger("litematic-portal");

	private FileHandler() {
	}

	/**
	 * Moves every {@value #EXTENSION} file from the top level of {@code sourceDir}
	 * into {@code targetDir}. The target directory is created if needed and files
	 * with the same name are replaced.
	 *
	 * @throws IOException if the source directory cannot be read or the target directory cannot be created.
	 *                     A single file that cannot be moved does not throw, it is counted as failed.
	 */
	public static ImportResult importFiles(Path sourceDir, Path targetDir) throws IOException {
		List<Path> files;
		try (Stream<Path> stream = Files.list(sourceDir)) {
			files = stream.filter(Files::isRegularFile)
					.filter(p -> p.getFileName().toString().toLowerCase(Locale.ROOT).endsWith(EXTENSION))
					.toList();
		}
		if (files.isEmpty()) {
			return new ImportResult(0, 0);
		}

		Files.createDirectories(targetDir);

		int moved = 0;
		int failed = 0;
		for (Path file : files) {
			try {
				Files.move(file, targetDir.resolve(file.getFileName()), StandardCopyOption.REPLACE_EXISTING);
				moved++;
			} catch (IOException e) {
				LOGGER.error("Could not move {}", file, e);
				failed++;
			}
		}
		return new ImportResult(moved, failed);
	}
}
