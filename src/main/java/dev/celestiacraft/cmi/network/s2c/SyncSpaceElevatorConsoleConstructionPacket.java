package dev.celestiacraft.cmi.network.s2c;

import dev.celestiacraft.cmi.client.render.SpaceElevatorConsoleScreenState;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SyncSpaceElevatorConsoleConstructionPacket {
	private final BlockPos consolePos;

	public SyncSpaceElevatorConsoleConstructionPacket(BlockPos consolePos) {
		this.consolePos = consolePos.immutable();
	}

	public static void encode(SyncSpaceElevatorConsoleConstructionPacket msg, FriendlyByteBuf buf) {
		buf.writeBlockPos(msg.consolePos);
	}

	public static SyncSpaceElevatorConsoleConstructionPacket decode(FriendlyByteBuf buf) {
		return new SyncSpaceElevatorConsoleConstructionPacket(buf.readBlockPos());
	}

	public static void handle(SyncSpaceElevatorConsoleConstructionPacket msg, Supplier<NetworkEvent.Context> ctxSupplier) {
		NetworkEvent.Context ctx = ctxSupplier.get();
		ctx.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> SpaceElevatorConsoleScreenState.markConstruction(msg.consolePos)));
		ctx.setPacketHandled(true);
	}
}
