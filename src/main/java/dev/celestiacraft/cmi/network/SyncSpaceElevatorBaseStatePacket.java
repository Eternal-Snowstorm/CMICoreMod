package dev.celestiacraft.cmi.network;

import dev.celestiacraft.cmi.event.PlanetsScreenBaseBuildClientHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SyncSpaceElevatorBaseStatePacket {
	private final ResourceKey<Level> dimension;
	private final ChunkPos spaceStationPos;
	private final boolean built;

	public SyncSpaceElevatorBaseStatePacket(ResourceKey<Level> dimension, ChunkPos spaceStationPos, boolean built) {
		this.dimension = dimension;
		this.spaceStationPos = spaceStationPos;
		this.built = built;
	}

	public static void encode(SyncSpaceElevatorBaseStatePacket msg, FriendlyByteBuf buf) {
		buf.writeResourceKey(msg.dimension);
		buf.writeChunkPos(msg.spaceStationPos);
		buf.writeBoolean(msg.built);
	}

	public static SyncSpaceElevatorBaseStatePacket decode(FriendlyByteBuf buf) {
		return new SyncSpaceElevatorBaseStatePacket(buf.readResourceKey(net.minecraft.core.registries.Registries.DIMENSION), buf.readChunkPos(), buf.readBoolean());
	}

	public static void handle(SyncSpaceElevatorBaseStatePacket msg, Supplier<NetworkEvent.Context> ctxSupplier) {
		NetworkEvent.Context ctx = ctxSupplier.get();
		ctx.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> PlanetsScreenBaseBuildClientHandler.updateBuiltState(msg.dimension, msg.spaceStationPos, msg.built)));
		ctx.setPacketHandled(true);
	}
}
