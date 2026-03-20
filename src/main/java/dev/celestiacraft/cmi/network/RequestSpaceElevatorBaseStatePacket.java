package dev.celestiacraft.cmi.network;

import dev.celestiacraft.cmi.compat.adastra.SpaceElevatorBaseHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.function.Supplier;

public class RequestSpaceElevatorBaseStatePacket {
	private final ResourceKey<Level> dimension;
	private final ChunkPos spaceStationPos;

	public RequestSpaceElevatorBaseStatePacket(ResourceKey<Level> dimension, ChunkPos spaceStationPos) {
		this.dimension = dimension;
		this.spaceStationPos = spaceStationPos;
	}

	public static void encode(RequestSpaceElevatorBaseStatePacket msg, FriendlyByteBuf buf) {
		buf.writeResourceKey(msg.dimension);
		buf.writeChunkPos(msg.spaceStationPos);
	}

	public static RequestSpaceElevatorBaseStatePacket decode(FriendlyByteBuf buf) {
		return new RequestSpaceElevatorBaseStatePacket(buf.readResourceKey(net.minecraft.core.registries.Registries.DIMENSION), buf.readChunkPos());
	}

	public static void handle(RequestSpaceElevatorBaseStatePacket msg, Supplier<NetworkEvent.Context> ctxSupplier) {
		NetworkEvent.Context ctx = ctxSupplier.get();
		ctx.enqueueWork(() -> {
			ServerPlayer player = ctx.getSender();
			if (player == null) {
				return;
			}

			ServerLevel level = player.server.getLevel(msg.dimension);
			if (level == null) {
				return;
			}

			boolean built = SpaceElevatorBaseHandler.hasBuiltStructure(level, msg.spaceStationPos);
			CmiNetwork.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new SyncSpaceElevatorBaseStatePacket(msg.dimension, msg.spaceStationPos, built));
		});
		ctx.setPacketHandled(true);
	}
}
