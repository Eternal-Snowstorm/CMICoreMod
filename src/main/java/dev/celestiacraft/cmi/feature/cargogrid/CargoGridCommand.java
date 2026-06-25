package dev.celestiacraft.cmi.feature.cargogrid;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import dev.celestiacraft.cmi.Cmi;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Cmi.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CargoGridCommand {
	@SubscribeEvent
	public static void onRegisterCommands(RegisterCommandsEvent event) {
		CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

		dispatcher.register(Commands.literal(Cmi.MODID)
				.then(Commands.literal("reload_grid")
						.requires((source) -> {
							return source.hasPermission(2);
						})
						.executes((context) -> {
							int loaded = CargoGridRules.load();
							CommandSourceStack source = context.getSource();
							if (loaded < 0) {
								source.sendFailure(Component.literal("[CMI] Failed to reload cargo grid rules — see log"));
								return 0;
							}
							final int count = loaded;
							source.sendSuccess(() -> {
								return Component.literal("[CMI] Cargo grid: " + count + " rule(s) loaded");
							}, true);
							return Command.SINGLE_SUCCESS;
						})
				)
		);
	}
}
