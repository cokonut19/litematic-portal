package cokonut19.litematicportal;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import org.lwjgl.glfw.GLFW;

import java.nio.file.Path;
import java.nio.file.Paths;

import static cokonut19.litematicportal.util.ClientUtil.*;

public class LitematicPortalClient implements ClientModInitializer {
	public static final String MOD_ID = "litematic-portal";

	@Override
	public void onInitializeClient() {
		var CATEGORY = addKeyCategory("keys");
		var KEYBIND = addKeybind(GLFW.GLFW_KEY_J,CATEGORY);
		ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
			if (client.player != null) {
				printToChat(MOD_ID + ": Hello!");
			}
		});
	}
}
