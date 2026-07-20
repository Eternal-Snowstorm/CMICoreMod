package dev.celestiacraft.cmi.common.block.space_elevator_base_console.transfer;

import dev.celestiacraft.cmi.Cmi;
import dev.celestiacraft.cmi.network.CmiNetwork;
import dev.celestiacraft.cmi.network.s2c.SyncSpaceElevatorConsoleTransferPacket;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;

@Mod.EventBusSubscriber(modid = Cmi.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class SpaceElevatorConsoleTransferHandler {
	private SpaceElevatorConsoleTransferHandler() {
	}

	@SubscribeEvent
	public static void onTransfer(SpaceElevatorConsoleTransferEvent event) {
		LevelChunk chunk = event.level().getChunkAt(event.consolePos());
		CmiNetwork.CHANNEL.send(
				PacketDistributor.TRACKING_CHUNK.with(() -> chunk),
				new SyncSpaceElevatorConsoleTransferPacket(event.consolePos())
		);
	}
}
