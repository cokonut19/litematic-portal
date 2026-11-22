package cokonut19.litematicportal;

import cokonut19.litematicportal.io.FileHandler;
import cokonut19.litematicportal.io.ImportResult;
import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

/**
 * Press the import key to move all .litematic files from the Downloads folder
 * into the Litematica schematics folder.
 */
public class LitematicPortalClient implements ClientModInitializer {
	public static final String MOD_ID = "litematic-portal";
	private static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	private KeyMapping importKey;
	private boolean importRunning = false;

	@Override
	public void onInitializeClient() {
		var category = new KeyMapping.Category(Identifier.fromNamespaceAndPath(MOD_ID, "main"));
		importKey = KeyMappingHelper.registerKeyMapping(
				new KeyMapping("key." + MOD_ID + ".import", InputConstants.KEY_J, category));

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			while (importKey.consumeClick()) {
				startImport(client);
			}
		});
	}

	private void startImport(Minecraft client) {
		if (importRunning) {
			return;
		}
		importRunning = true;

		Path source = Path.of(System.getProperty("user.home"), "Downloads");
		Path target = FabricLoader.getInstance().getGameDir().resolve("schematics");

		CompletableFuture
				.supplyAsync(() -> importSafely(source, target), Util.ioPool())
				.handleAsync((message, error) -> {
					importRunning = false;
					if (error != null) {
						LOGGER.error("Import crashed", error);
					}
					if (client.player != null) {
						client.player.sendSystemMessage(error == null ? message : errorMessage(source));
					}
					return null;
				}, client);
	}

	private static Component importSafely(Path source, Path target) {
		try {
			ImportResult result = FileHandler.importFiles(source, target);
			if (result.moved() == 0 && result.failed() == 0) {
				return Component.translatable("message.litematic-portal.none", source.toString());
			}
			if (result.failed() > 0) {
				return Component.translatable("message.litematic-portal.partial", result.moved(), result.failed());
			}
			return Component.translatable("message.litematic-portal.done", result.moved());
		} catch (NoSuchFileException e) {
			return Component.translatable("message.litematic-portal.missing", source.toString());
		} catch (IOException | RuntimeException e) {
			LOGGER.error("Import from {} failed", source, e);
			return errorMessage(source);
		}
	}

	private static Component errorMessage(Path source) {
		return Component.translatable("message.litematic-portal.error", source.toString());
	}
}
