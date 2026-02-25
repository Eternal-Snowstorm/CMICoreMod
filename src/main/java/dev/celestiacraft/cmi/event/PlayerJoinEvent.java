package dev.celestiacraft.cmi.event;

import dev.celestiacraft.cmi.Cmi;
import dev.celestiacraft.cmi.network.CmiNetwork;
import dev.celestiacraft.cmi.network.SeedPacket;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;
import net.minecraft.server.level.ServerPlayer;

@SuppressWarnings("ALL")
@Mod.EventBusSubscriber(modid = Cmi.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class PlayerJoinEvent {
	@SubscribeEvent
	public static void onLogin(PlayerEvent.PlayerLoggedInEvent event) {
		if (!event.getEntity().level().isClientSide()) {
			ServerPlayer player = (ServerPlayer) event.getEntity();
			long seed = player.server.overworld().getSeed();

			CmiNetwork.CHANNEL.send(
					PacketDistributor.PLAYER.with(() -> player),
					new SeedPacket(seed)
			);
		}
	}
}