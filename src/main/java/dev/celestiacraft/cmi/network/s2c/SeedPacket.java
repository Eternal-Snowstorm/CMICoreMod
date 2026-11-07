package dev.celestiacraft.cmi.network.s2c;

import dev.celestiacraft.cmi.network.ClientSeedHandler;
import lombok.AllArgsConstructor;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

@AllArgsConstructor
public class SeedPacket {
	private final long seed;

	public static void encode(SeedPacket packet, FriendlyByteBuf buf) {
		buf.writeLong(packet.seed);
	}

	public static SeedPacket decode(FriendlyByteBuf buf) {
		return new SeedPacket(buf.readLong());
	}

	public static void handle(SeedPacket packet, Supplier<NetworkEvent.Context> supplier) {
		NetworkEvent.Context context = supplier.get();

		context.enqueueWork(() -> {
			ClientSeedHandler.writeValue(packet.seed);
		});
		context.setPacketHandled(true);
	}
}