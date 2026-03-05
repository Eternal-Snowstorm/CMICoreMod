package dev.celestiacraft.cmi.event;

import dev.celestiacraft.cmi.Cmi;
import dev.celestiacraft.cmi.compat.ModCompat;
import dev.celestiacraft.cmi.compat.adastra.AdAstraOxygenCompat;
import dev.celestiacraft.cmi.compat.create.CreateOxygenSupport;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.living.LivingBreatheEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Cmi.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class PlayerDrownInNether {
	@SubscribeEvent
	public static void onLivingBreathe(LivingBreatheEvent event) {
		LivingEntity entity = event.getEntity();
		if (!(entity instanceof Player player)) {
			return;
		}

		Level level = player.level();
		if (level.dimension() != Level.NETHER) {
			return;
		}

		if (player.isCreative() || player.isSpectator()) {
			return;
		}

		boolean hasCreateBacktankSupport = CreateOxygenSupport.hasBacktankSupport(player);
		boolean hasAdAstraSuitSupport = ModCompat.isAdAstraLoaded() && AdAstraOxygenCompat.hasAdAstraSpaceSuitSupport(player);
		boolean hasOxygenSupport = hasCreateBacktankSupport || hasAdAstraSuitSupport;

		if (level.isClientSide()) {
			if (hasCreateBacktankSupport) {
				player.getPersistentData().putInt("VisualBacktankAir", CreateOxygenSupport.getVisualBacktankAir(player));
			} else {
				player.getPersistentData().remove("VisualBacktankAir");
			}
		}

		if (hasOxygenSupport) {
			event.setCanBreathe(true);
			event.setCanRefillAir(true);
			event.setConsumeAirAmount(0);
			event.setRefillAirAmount(player.getMaxAirSupply());

			if (!level.isClientSide()) {
				if (hasCreateBacktankSupport && level.getGameTime() % 20 == 0) {
					CreateOxygenSupport.consumeBacktankAir(player, 1);
				} else if (hasAdAstraSuitSupport && level.getGameTime() % 12 == 0) {
					AdAstraOxygenCompat.consumeAdAstraSuitOxygen(player, 1);
				}
			}
			return;
		}

		event.setCanBreathe(false);
		event.setCanRefillAir(false);
		event.setConsumeAirAmount(1);
		event.setRefillAirAmount(0);
	}
}
