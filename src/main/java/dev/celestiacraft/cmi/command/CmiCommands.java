package dev.celestiacraft.cmi.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import dev.celestiacraft.cmi.Cmi;
import dev.celestiacraft.cmi.client.exporter.StructureExportScreen;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterClientCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

@Mod.EventBusSubscriber(modid = Cmi.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class CmiCommands {

    @SubscribeEvent
    public static void onRegisterClientCommands(RegisterClientCommandsEvent event) {
        event.getDispatcher().register(
                literal("cmi")
                        .then(literal("export")
                                .executes(ctx -> {
                                    Minecraft.getInstance().tell(() ->
                                            Minecraft.getInstance().setScreen(new StructureExportScreen()));
                                    return 1;
                                })
                                .then(argument("path", StringArgumentType.greedyString())
                                        .executes(ctx -> {
                                            String path = StringArgumentType.getString(ctx, "path");
                                            Minecraft.getInstance().tell(() ->
                                                    Minecraft.getInstance().setScreen(new StructureExportScreen(path)));
                                            return 1;
                                        })
                                )
                        )
        );
    }
}
