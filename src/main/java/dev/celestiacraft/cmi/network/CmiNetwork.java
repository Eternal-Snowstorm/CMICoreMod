package dev.celestiacraft.cmi.network;

import dev.celestiacraft.cmi.Cmi;
import dev.celestiacraft.cmi.network.c2s.*;
import dev.celestiacraft.cmi.network.s2c.SeedPacket;
import dev.celestiacraft.cmi.network.s2c.SyncSpaceElevatorBaseStatePacket;
import dev.celestiacraft.cmi.network.s2c.SyncSpaceElevatorMaterialsPacket;
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
		CHANNEL.registerMessage(
				id++,
				BuildEarthSpaceElevatorBasePacket.class,
				BuildEarthSpaceElevatorBasePacket::encode,
				BuildEarthSpaceElevatorBasePacket::decode,
				BuildEarthSpaceElevatorBasePacket::handle
		);
		CHANNEL.registerMessage(
				id++,
				RequestSpaceElevatorBaseStatePacket.class,
				RequestSpaceElevatorBaseStatePacket::encode,
				RequestSpaceElevatorBaseStatePacket::decode,
				RequestSpaceElevatorBaseStatePacket::handle
		);
		CHANNEL.registerMessage(
				id++,
				SyncSpaceElevatorBaseStatePacket.class,
				SyncSpaceElevatorBaseStatePacket::encode,
				SyncSpaceElevatorBaseStatePacket::decode,
				SyncSpaceElevatorBaseStatePacket::handle
		);
		CHANNEL.registerMessage(
				id++,
				ConstructSpaceElevatorPacket.class,
				ConstructSpaceElevatorPacket::encode,
				ConstructSpaceElevatorPacket::decode,
				ConstructSpaceElevatorPacket::handle
		);
		CHANNEL.registerMessage(
				id++,
				RequestSpaceElevatorMaterialsPacket.class,
				RequestSpaceElevatorMaterialsPacket::encode,
				RequestSpaceElevatorMaterialsPacket::decode,
				RequestSpaceElevatorMaterialsPacket::handle
		);
		CHANNEL.registerMessage(
				id++,
				StoreSpaceElevatorMaterialsPacket.class,
				StoreSpaceElevatorMaterialsPacket::encode,
				StoreSpaceElevatorMaterialsPacket::decode,
				StoreSpaceElevatorMaterialsPacket::handle
		);
		CHANNEL.registerMessage(
				id++,
				SyncSpaceElevatorMaterialsPacket.class,
				SyncSpaceElevatorMaterialsPacket::encode,
				SyncSpaceElevatorMaterialsPacket::decode,
				SyncSpaceElevatorMaterialsPacket::handle
		);
		CHANNEL.registerMessage(
				id++,
				StartSpaceElevatorTransportPacket.class,
				StartSpaceElevatorTransportPacket::encode,
				StartSpaceElevatorTransportPacket::decode,
				StartSpaceElevatorTransportPacket::handle
		);
	}
}