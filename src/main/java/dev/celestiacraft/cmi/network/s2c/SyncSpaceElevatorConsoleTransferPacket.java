package dev.celestiacraft.cmi.network.s2c;

import dev.celestiacraft.cmi.client.render.SpaceElevatorConsoleScreenState;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SyncSpaceElevatorConsoleTransferPacket {
	private final BlockPos consolePos;

	public SyncSpaceElevatorConsoleTransferPacket(BlockPos consolePos) {
		this.consolePos = consolePos.immutable();
	}

	public static void encode(SyncSpaceElevatorConsoleTransferPacket packet, FriendlyByteBuf buf) {
		buf.writeBlockPos(packet.consolePos);
	}

	public static SyncSpaceElevatorConsoleTransferPacket decode(FriendlyByteBuf buf) {
		return new SyncSpaceElevatorConsoleTransferPacket(buf.readBlockPos());
	}

	public static void handle(SyncSpaceElevatorConsoleTransferPacket packet, Supplier<NetworkEvent.Context> supplier) {
		NetworkEvent.Context context = supplier.get();
		context.enqueueWork(() -> {
			DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> {
				return () -> {
					return SpaceElevatorConsoleScreenState.markTransfer(packet.consolePos);
				};
			});
		});
		context.setPacketHandled(true);
	}
}
