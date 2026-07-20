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

	public static void encode(SyncSpaceElevatorConsoleTransferPacket msg, FriendlyByteBuf buf) {
		buf.writeBlockPos(msg.consolePos);
	}

	public static SyncSpaceElevatorConsoleTransferPacket decode(FriendlyByteBuf buf) {
		return new SyncSpaceElevatorConsoleTransferPacket(buf.readBlockPos());
	}

	public static void handle(SyncSpaceElevatorConsoleTransferPacket msg, Supplier<NetworkEvent.Context> ctxSupplier) {
		NetworkEvent.Context ctx = ctxSupplier.get();
		ctx.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> SpaceElevatorConsoleScreenState.markTransfer(msg.consolePos)));
		ctx.setPacketHandled(true);
	}
}
