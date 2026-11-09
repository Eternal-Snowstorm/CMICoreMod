package dev.celestiacraft.cmi.network.s2c;

import dev.celestiacraft.cmi.event.SpaceElevatorWrenchClientHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SyncSpaceElevatorMaterialsPacket {
	private final BlockPos anchorPos;
	private final int[] counts;
	private final int[] fluidAmounts;
	private final boolean orbitalCounterpartPresent;

	public SyncSpaceElevatorMaterialsPacket(BlockPos anchorPos, int[] counts, int[] fluidAmounts) {
		this(anchorPos, counts, fluidAmounts, false);
	}

	public SyncSpaceElevatorMaterialsPacket(BlockPos anchorPos, int[] counts, int[] fluidAmounts, boolean orbitalCounterpartPresent) {
		this.anchorPos = anchorPos.immutable();
		this.counts = counts.clone();
		this.fluidAmounts = fluidAmounts.clone();
		this.orbitalCounterpartPresent = orbitalCounterpartPresent;
	}

	public static void encode(SyncSpaceElevatorMaterialsPacket packet, FriendlyByteBuf buf) {
		buf.writeBlockPos(packet.anchorPos);
		buf.writeVarInt(packet.counts.length);
		for (int count : packet.counts) {
			buf.writeVarInt(count);
		}
		buf.writeVarInt(packet.fluidAmounts.length);
		for (int amount : packet.fluidAmounts) {
			buf.writeVarInt(amount);
		}
		buf.writeBoolean(packet.orbitalCounterpartPresent);
	}

	public static SyncSpaceElevatorMaterialsPacket decode(FriendlyByteBuf buf) {
		BlockPos anchorPos = buf.readBlockPos();
		int size = buf.readVarInt();
		int[] counts = new int[size];
		for (int i = 0; i < size; i++) {
			counts[i] = buf.readVarInt();
		}
		int fluidSize = buf.readVarInt();
		int[] fluidAmounts = new int[fluidSize];
		for (int i = 0; i < fluidSize; i++) {
			fluidAmounts[i] = buf.readVarInt();
		}
		return new SyncSpaceElevatorMaterialsPacket(anchorPos, counts, fluidAmounts, buf.readBoolean());
	}

	public static void handle(SyncSpaceElevatorMaterialsPacket packet, Supplier<NetworkEvent.Context> supplier) {
		NetworkEvent.Context context = supplier.get();
		context.enqueueWork(() -> {
			DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> {
				return () -> {
					SpaceElevatorWrenchClientHandler.syncStoredCounts(packet.anchorPos, packet.counts, packet.fluidAmounts, packet.orbitalCounterpartPresent);
				};
			});
		});
		context.setPacketHandled(true);
	}
}