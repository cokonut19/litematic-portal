package cokonut19.litematicportal;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.resources.Identifier;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LitematicPortalClient implements ClientModInitializer {
	public static final String MOD_ID = "litematic-portal";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static final FabricLoader LOADER = FabricLoader.getInstance();
	public static final Minecraft MINECRAFT = Minecraft.getInstance();

	@Override
	public void onInitializeClient() {
		var CATEGORY = addKeyCategory("keys");
		var KEYBIND = addKeybind(CATEGORY);
		ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
			if (client.player != null) {
				printToChat("Hello!");
			}
		});
		}
	public KeyMapping addKeybind(KeyMapping.Category CATEGORY) {
		return KeyBindingHelper.registerKeyBinding(
				new KeyMapping(
						"key.%s.importFiles".formatted(MOD_ID),
						InputConstants.Type.KEYSYM, // Keyboard Keybinding
						GLFW.GLFW_KEY_J, //J-Key on QWERTY
						CATEGORY
				)
		);
	}

	public KeyMapping.Category addKeyCategory(String name) {
		return new KeyMapping.Category(
				Identifier.fromNamespaceAndPath(MOD_ID, name)
		);
	}

	public static void printToChat(String message) {
		var MINECRAFT = Minecraft.getInstance();
		if (MINECRAFT.player != null) {
			MINECRAFT.player.displayClientMessage(Component.literal(message), false);
		}
	}
}