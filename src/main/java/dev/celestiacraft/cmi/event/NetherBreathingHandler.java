package dev.celestiacraft.cmi.event;

import dev.celestiacraft.cmi.Cmi;
import dev.celestiacraft.cmi.compat.ModCompat;
import dev.celestiacraft.cmi.compat.adastra.AdAstraOxygenCompat;
import dev.celestiacraft.cmi.compat.create.CreateOxygenSupport;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.living.LivingBreatheEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Cmi.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class NetherBreathingHandler {
	@SubscribeEvent
	public static void onLivingBreathe(LivingBreatheEvent event) {
		if (!(event.getEntity() instanceof Player player)) {
			return;
		}

		Level level = player.level();
		if (level.dimension() != Level.NETHER) {
			return;
		}

		if (player.isCreative() || player.isSpectator()) {
			return;
		}

		boolean hasCreateSupport = CreateOxygenSupport.hasBacktankSupport(player);
		boolean hasAdAstraSupport = ModCompat.isAdAstraLoaded() && AdAstraOxygenCompat.hasSpaceSuitSupport(player);

		if (level.isClientSide()) {
			if (hasCreateSupport) {
				player.getPersistentData().putInt("VisualBacktankAir", CreateOxygenSupport.getVisualBacktankAir(player));
			} else {
				player.getPersistentData().remove("VisualBacktankAir");
			}
		}

		if (hasCreateSupport) {
			event.setCanBreathe(true);
			event.setCanRefillAir(false);
			event.setConsumeAirAmount(0);
			event.setRefillAirAmount(0);
			player.setAirSupply(player.getMaxAirSupply() - 1);

			if (!level.isClientSide() && level.getGameTime() % 20 == 0) {
				CreateOxygenSupport.consumeBacktankAir(player, 1);
			}
			return;
		}

		if (hasAdAstraSupport) {
			event.setCanBreathe(true);
			event.setCanRefillAir(true);
			event.setConsumeAirAmount(0);
			event.setRefillAirAmount(player.getMaxAirSupply());

			if (!level.isClientSide() && level.getGameTime() % 12 == 0) {
				AdAstraOxygenCompat.consumeSuitOxygen(player, 1);
			}
			return;
		}

		event.setCanBreathe(false);
		event.setCanRefillAir(false);
		event.setConsumeAirAmount(1);
		event.setRefillAirAmount(0);
	}
}
