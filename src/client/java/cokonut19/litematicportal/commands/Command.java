package cokonut19.litematicportal.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.client.command.v2.*;
import net.minecraft.network.chat.Component;
import java.nio.file.Paths;

import cokonut19.litematicportal.io.FileHandler;
import static  cokonut19.litematicportal.util.ClientUtil.*;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal; // enables usage without class.field usage, just field

public class Command {
    public static void registerCommands() {
        // TODO Error Handling and chat messages already implemented in FileHandler methods
        // in the future it could improve UX by validating paths directly in the setters here
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(
                    literal("lp")
                            .then(literal("import"))
                            .executes(context -> {
                                FileHandler.moveFilesAsync(getSourceDir(), getTargetDir());
                                return 0;
                            })
                            // Setter
                            .then(literal("set_source_directory"))
                            .then(argument("path", StringArgumentType.greedyString()))
                            .executes(context -> {
                                String path = StringArgumentType.getString(context, "path");
                                setSourceDir(Paths.get(path));
                                context.getSource().sendFeedback(Component.literal("Set source directory to " + path));  //literal != Component.literal
                                return 0;
                            })
                            .then(literal("set_target_directory"))
                            .then(argument("path", StringArgumentType.greedyString()))
                            .executes(context -> {
                                String path = StringArgumentType.getString(context, "path");
                                setTargetDir(Paths.get(path));
                                context.getSource().sendFeedback(Component.literal("Set target directory to " + path));  //literal != Component.literal
                                return 0;
                            })
                            // Getter
                            .then(literal("get_source_directory"))
                            .executes(context -> {
                                context.getSource().sendFeedback(Component.literal(getSourceDir().toString()));
                                return 0;
                            })
                            .then(literal("get_target_directory"))
                            .executes(context -> {
                                context.getSource().sendFeedback(Component.literal(getTargetDir().toString()));
                                return 0;
                            })
            );
        });
    }
}
