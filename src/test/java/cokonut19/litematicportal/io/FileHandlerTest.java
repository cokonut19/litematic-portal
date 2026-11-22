package cokonut19.litematicportal.io;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FileHandlerTest {
	@TempDir
	Path tmp;

	private Path source() throws IOException {
		return Files.createDirectories(tmp.resolve("downloads"));
	}

	private Path target() {
		return tmp.resolve("schematics");
	}

	@Test
	void movesLitematicFilesIntoTheTargetDirectory() throws IOException {
		Path source = source();
		Files.writeString(source.resolve("house.litematic"), "house");
		Files.writeString(source.resolve("tower.litematic"), "tower");

		ImportResult result = FileHandler.importFiles(source, target());

		assertEquals(new ImportResult(2, 0), result);
		assertEquals("house", Files.readString(target().resolve("house.litematic")));
		assertEquals("tower", Files.readString(target().resolve("tower.litematic")));
		assertFalse(Files.exists(source.resolve("house.litematic")));
		assertFalse(Files.exists(source.resolve("tower.litematic")));
	}

	@Test
	void leavesOtherFilesAndSubdirectoriesUntouched() throws IOException {
		Path source = source();
		Files.writeString(source.resolve("notes.txt"), "notes");
		Path nested = Files.createDirectories(source.resolve("old"));
		Files.writeString(nested.resolve("nested.litematic"), "nested");

		ImportResult result = FileHandler.importFiles(source, target());

		assertEquals(new ImportResult(0, 0), result);
		assertTrue(Files.exists(source.resolve("notes.txt")));
		assertTrue(Files.exists(nested.resolve("nested.litematic")));
	}

	@Test
	void replacesAFileWithTheSameNameInTheTarget() throws IOException {
		Path source = source();
		Files.writeString(source.resolve("house.litematic"), "new");
		Files.createDirectories(target());
		Files.writeString(target().resolve("house.litematic"), "old");

		ImportResult result = FileHandler.importFiles(source, target());

		assertEquals(new ImportResult(1, 0), result);
		assertEquals("new", Files.readString(target().resolve("house.litematic")));
	}

	@Test
	void matchesTheExtensionRegardlessOfCase() throws IOException {
		Path source = source();
		Files.writeString(source.resolve("Castle.LITEMATIC"), "castle");

		ImportResult result = FileHandler.importFiles(source, target());

		assertEquals(new ImportResult(1, 0), result);
		assertTrue(Files.exists(target().resolve("Castle.LITEMATIC")));
	}

	@Test
	void failsWithoutCreatingAnythingWhenTheSourceDirectoryIsMissing() {
		Path missing = tmp.resolve("does-not-exist");

		assertThrows(NoSuchFileException.class, () -> FileHandler.importFiles(missing, target()));

		assertFalse(Files.exists(missing));
		assertFalse(Files.exists(target()));
	}

	@Test
	void doesNotCreateTheTargetDirectoryWhenThereIsNothingToMove() throws IOException {
		ImportResult result = FileHandler.importFiles(source(), target());

		assertEquals(new ImportResult(0, 0), result);
		assertFalse(Files.exists(target()));
	}
}
