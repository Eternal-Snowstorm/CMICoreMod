package dev.celestiacraft.cmi.network.c2s;

import dev.celestiacraft.cmi.common.entity.space_elevator.SpaceElevatorEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class StartSpaceElevatorTransportPacket {
	private final int entityId;

	public StartSpaceElevatorTransportPacket(int entityId) {
		this.entityId = entityId;
	}

	public static void encode(StartSpaceElevatorTransportPacket msg, FriendlyByteBuf buf) {
		buf.writeVarInt(msg.entityId);
	}

	public static StartSpaceElevatorTransportPacket decode(FriendlyByteBuf buf) {
		return new StartSpaceElevatorTransportPacket(buf.readVarInt());
	}

	public static void handle(StartSpaceElevatorTransportPacket msg, Supplier<NetworkEvent.Context> ctxSupplier) {
		NetworkEvent.Context ctx = ctxSupplier.get();
		ctx.enqueueWork(() -> {
			ServerPlayer player = ctx.getSender();
			if (player == null) {
				return;
			}
			if (!(player.getVehicle() instanceof SpaceElevatorEntity elevator) || elevator.getId() != msg.entityId) {
				return;
			}
			elevator.requestLaunch(player);
		});
		ctx.setPacketHandled(true);
	}
}
