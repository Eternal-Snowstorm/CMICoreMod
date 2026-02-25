package dev.celestiacraft.cmi.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SeedPacket {

	private final long seed;

	public SeedPacket(long seed) {
		this.seed = seed;
	}

	public static void encode(SeedPacket msg, FriendlyByteBuf buf) {
		buf.writeLong(msg.seed);
	}

	public static SeedPacket decode(FriendlyByteBuf buf) {
		return new SeedPacket(buf.readLong());
	}

	public static void handle(SeedPacket msg, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			ClientSeedHandler.writeValue(msg.seed);
		});
		ctx.get().setPacketHandled(true);
	}
}