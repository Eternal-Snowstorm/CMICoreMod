package dev.celestiacraft.cmi.network.s2c;

import dev.celestiacraft.cmi.compat.thermal.ThermalDynamoClient;
import lombok.AllArgsConstructor;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

@AllArgsConstructor
public class SyncDynamoEnergyRatePacket {
	private final BlockPos pos;
	private final int energyPerTick;

	public static void encode(SyncDynamoEnergyRatePacket msg, FriendlyByteBuf buf) {
		buf.writeBlockPos(msg.pos);
		buf.writeVarInt(msg.energyPerTick);
	}

	public static SyncDynamoEnergyRatePacket decode(FriendlyByteBuf buf) {
		return new SyncDynamoEnergyRatePacket(buf.readBlockPos(), buf.readVarInt());
	}

	public static void handle(SyncDynamoEnergyRatePacket message, Supplier<NetworkEvent.Context> supplier) {
		NetworkEvent.Context context = supplier.get();

		context.enqueueWork(() -> {
			DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> {
				return () -> {
					ThermalDynamoClient.updateEnergyRate(message.pos, message.energyPerTick);
				};
			});
		});
		context.setPacketHandled(true);
	}
}