package dev.celestiacraft.cmi.network;

import dev.celestiacraft.cmi.Cmi;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class CmiNetwork {
	private static final String PROTOCOL = "1";

	public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
			Cmi.loadResource("main"),
			() -> PROTOCOL,
			PROTOCOL::equals,
			PROTOCOL::equals
	);

	private static int id = 0;

	public static void register() {
		CHANNEL.registerMessage(
				id++,
				SeedPacket.class,
				SeedPacket::encode,
				SeedPacket::decode,
				SeedPacket::handle
		);
	}
}