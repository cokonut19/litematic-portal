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

import static cokonut19.litematicportal.util.ClientUtil.*;

public class LitematicPortalClient implements ClientModInitializer {
	public static final String MOD_ID = "litematic-portal";

	@Override
	public void onInitializeClient() {
		var CATEGORY = addKeyCategory("keys");
		var KEYBIND = addKeybind(CATEGORY);
		ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
			if (client.player != null) {
				printToChat(MOD_ID + ": Hello!");
			}
		});
	}
}