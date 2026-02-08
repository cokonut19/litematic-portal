package cokonut19.litematicportal.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ClientUtil {
    private static final String MOD_ID = "litematic-portal";
    private static final String configPath = getConfigDir().resolve("litematic-portal.json").toString();
    private static Config configInstance = loadConfig();


    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

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

    public static Path getGameDir() {
        return getFabricLoader().getGameDir();
    }
    public static Path getConfigDir() {
        return getFabricLoader().getConfigDir();
    }

    public static Path getDownloadDir() {
        var home = System.getProperty("user.home");
        return Paths.get(home + "/Downloads");
    }
    public static Path getSchematicsDir() {
        return getGameDir().resolve("schematics");
    }

    public static void printToChat(String message) {
        if (getPlayerClient() != null) {
            getPlayerClient().displayClientMessage(Component.literal(message), false);
        }
    }
    public static KeyMapping addKeybind(int key, KeyMapping.Category CATEGORY) {
        return KeyBindingHelper.registerKeyBinding(
                new KeyMapping(
                        "key.%s.importFiles".formatted(MOD_ID),
                        InputConstants.Type.KEYSYM, // Keyboard Keybinding
                        key, //J-Key on QWERTY
                        CATEGORY
                )
        );
    }
    public static KeyMapping.Category addKeyCategory(String name) {
        return new KeyMapping.Category(
                Identifier.fromNamespaceAndPath(MOD_ID, name)
        );
    }

    public static Config getConfigInstance() {
        if (configInstance != null) {
            return configInstance;
        }
        return loadConfig();
    }
    public static void saveConfig() {
        try (var w = new BufferedWriter(new FileWriter(configPath))) {
            GSON.toJson(configInstance, w);
        } catch (IOException e) {
            getLoggerWithID().error(e.getMessage());
            printToChat("Failed to save config.");
        }
    }
    // Only intended to be used at startup
    public static Config loadConfig() {
        try (var r = new BufferedReader(new FileReader(configPath))) {
            configInstance = GSON.fromJson(r, Config.class);
        } catch (IOException e) {
            getLoggerWithID().error(e.getMessage());
            printToChat("Failed to load config. Proceeding with defaults.");
            configInstance = new Config(getDownloadDir().toString(), getSchematicsDir().toString());
        }
        return configInstance;
    }
    public static void updateConfig(Config newConfig) {
        configInstance = newConfig;
        saveConfig();
    }
}
