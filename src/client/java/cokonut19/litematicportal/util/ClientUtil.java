package cokonut19.litematicportal.util;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;

public class ClientUtil {
    public static final String MOD_ID = "litematic-portal";
    private static Path SOURCE_DIR;
    private static Path TARGET_DIR;

    public static Logger getLoggerWithID() {
        return LoggerFactory.getLogger(MOD_ID);
    }
    public static Player getPlayerClient() {
        return Minecraft.getInstance().player;
    }
    public static Minecraft getClient() {
        return Minecraft.getInstance();
    }
    public static FabricLoader getFabricLoader () {
        return FabricLoader.getInstance();
    }

    public static void printToChat(String message) {
        if (getPlayerClient() != null) {
            getPlayerClient().displayClientMessage(Component.literal(message), false);
        }
    }
    public static KeyMapping addKeybind(KeyMapping.Category CATEGORY) {
        return KeyBindingHelper.registerKeyBinding(
                new KeyMapping(
                        "key.%s.importFiles".formatted(MOD_ID),
                        InputConstants.Type.KEYSYM, // Keyboard Keybinding
                        GLFW.GLFW_KEY_J, //J-Key on QWERTY
                        CATEGORY
                )
        );
    }
    public static KeyMapping.Category addKeyCategory(String name) {
        return new KeyMapping.Category(
                Identifier.fromNamespaceAndPath(MOD_ID, name)
        );
    }

    public static Path getTargetDir() {
        return TARGET_DIR;
    }
    public static void setTargetDir(Path targetDir) {
        TARGET_DIR = targetDir;
    }
    public static Path getSourceDir() {
        return SOURCE_DIR;
    }
    public static void setSourceDir(Path sourceDir) {
        SOURCE_DIR = sourceDir;
    }
}
