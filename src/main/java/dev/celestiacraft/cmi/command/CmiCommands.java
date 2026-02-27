package dev.celestiacraft.cmi.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import dev.celestiacraft.cmi.Cmi;
import dev.celestiacraft.cmi.client.exporter.StructureExportScreen;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterClientCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.nio.file.Files;
import java.nio.file.Path;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

@Mod.EventBusSubscriber(modid = Cmi.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class CmiCommands {

    @SubscribeEvent
    public static void onRegisterClientCommands(RegisterClientCommandsEvent event) {
        event.getDispatcher().register(
                literal("nebula")
                        .then(literal("export")
                                .executes(ctx -> {
                                    Minecraft.getInstance().tell(() ->
                                            Minecraft.getInstance().setScreen(new StructureExportScreen()));
                                    return 1;
                                })
                                .then(argument("file", StringArgumentType.string())
                                        .suggests((ctx, builder) -> {
                                            Path schematicsDir = Minecraft.getInstance().gameDirectory.toPath().resolve("schematics");
                                            if (Files.isDirectory(schematicsDir)) {
                                                try (var stream = Files.list(schematicsDir)) {
                                                    stream.filter(p -> p.toString().endsWith(".nbt"))
                                                            .map(p -> p.getFileName().toString())
                                                            .filter(name -> name.toLowerCase().startsWith(builder.getRemainingLowerCase()))
                                                            .forEach(builder::suggest);
                                                } catch (Exception ignored) {}
                                            }
                                            return builder.buildFuture();
                                        })
                                        .executes(ctx -> {
                                            String file = StringArgumentType.getString(ctx, "file");
                                            Minecraft.getInstance().tell(() ->
                                                    Minecraft.getInstance().setScreen(new StructureExportScreen(file)));
                                            return 1;
                                        })
                                )
                        )
        );
    }
}
