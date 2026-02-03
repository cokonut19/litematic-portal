package cokonut19.litematicportal;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;
import org.lwjgl.glfw.GLFW;

public class LitematicPortalClient implements ClientModInitializer {
	static String MOD_ID = "litematic-portal";
	@Override
	public void onInitializeClient() {
		var CATEGORY = addKeyCategory("keys");
		var KEYBIND = addKeybind(CATEGORY);
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
}