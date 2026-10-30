package dev.celestiacraft.cmi.network.s2c;

import dev.celestiacraft.cmi.common.grill.CmiGrillSources;
import lombok.AllArgsConstructor;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

@AllArgsConstructor
public class SyncGrillSourcesPacket {
	private static final int MAX_LENGTH = 262144;

	private final String json;

	// 写出整张表的 JSON 文本
	public static void encode(SyncGrillSourcesPacket packet, FriendlyByteBuf buf) {
		buf.writeUtf(packet.json, MAX_LENGTH);
	}

	// 读回整张表的 JSON 文本
	public static SyncGrillSourcesPacket decode(FriendlyByteBuf buf) {
		return new SyncGrillSourcesPacket(buf.readUtf(MAX_LENGTH));
	}

	// 客户端在主线程应用配置
	public static void handle(SyncGrillSourcesPacket packet, Supplier<NetworkEvent.Context> supplier) {
		NetworkEvent.Context context = supplier.get();

		context.enqueueWork(() -> {
			DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> {
				return () -> {
					CmiGrillSources.applyClient(packet.json);
				};
			});
		});
		context.setPacketHandled(true);
	}
}
