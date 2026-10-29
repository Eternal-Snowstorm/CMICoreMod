package dev.celestiacraft.cmi.network.s2c;

import dev.celestiacraft.cmi.common.grill.CmiGrillSources;
import lombok.AllArgsConstructor;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * 服务端 -> 客户端: 同步 grill_sources 配置
 *
 * <p>
 * 客户端 JEI 动画与客户端侧的 {@code BasinBlockEntity.getHeatLevelOf} 都要用同一份配置,
 * 所以数据包重载完成后把整张表发下来(体积很小, 几十条声明)
 * </p>
 */
@AllArgsConstructor
public class SyncGrillSourcesPacket {
	private static final int MAX_LENGTH = 262144;

	private final String json;

	public static void encode(SyncGrillSourcesPacket packet, FriendlyByteBuf buf) {
		buf.writeUtf(packet.json, MAX_LENGTH);
	}

	public static SyncGrillSourcesPacket decode(FriendlyByteBuf buf) {
		return new SyncGrillSourcesPacket(buf.readUtf(MAX_LENGTH));
	}

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
