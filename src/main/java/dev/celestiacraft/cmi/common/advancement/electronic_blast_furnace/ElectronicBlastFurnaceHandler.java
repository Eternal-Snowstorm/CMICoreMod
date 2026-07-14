package dev.celestiacraft.cmi.common.advancement.electronic_blast_furnace;

import com.lowdragmc.mbd2.common.machine.MBDMachine;
import com.lowdragmc.mbd2.common.machine.definition.config.event.MachineStructureFormedEvent;
import dev.celestiacraft.cmi.Cmi;
import dev.celestiacraft.cmi.common.register.CmiAdvanmentTrigger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber(modid = Cmi.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ElectronicBlastFurnaceHandler {
	@SubscribeEvent
	public static void onStructureFormed(MachineStructureFormedEvent event) {
		MBDMachine machine = event.getMachine();
		ResourceLocation name = machine.getDefinition().id();
		List<? extends Player> players = machine.getLevel().players();

		if (name.equals(Cmi.loadResource("electronic_blast_furnace"))) {
			players.forEach((player) -> {
				if (player instanceof ServerPlayer serverPlayer) {
					CmiAdvanmentTrigger.ELECTRONIC_BLAST_FURNACE.trigger(serverPlayer);
				}
			});
		}
	}
}