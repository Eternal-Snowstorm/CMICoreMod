package dev.celestiacraft.cmi.common.item.mechanism;

import dev.celestiacraft.cmi.Cmi;
import dev.celestiacraft.cmi.common.item.MechanismItem;
import dev.celestiacraft.cmi.common.register.CmiMechanism;
import dev.celestiacraft.libs.compat.curios.CuriosUtils;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Cmi.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CobaltItem extends MechanismItem {
	public CobaltItem(Properties properties) {
		super(properties);
	}

	@SubscribeEvent
	public static void onPlayerTick(LivingEvent.LivingTickEvent event) {
		LivingEntity entity = event.getEntity();
		Level level = entity.level();

		if (level.isClientSide()) {
			return;
		}

		if (entity instanceof Player player) {
			if ((player.tickCount % 20) * 15 != 0) {
				return;
			}

			if (CuriosUtils.hasItem(player, CmiMechanism.COBALT.get())) {
				player.addEffect(new MobEffectInstance(
						MobEffects.MOVEMENT_SPEED,
						20 * 20,
						1,
						false,
						false
				));
				player.addEffect(new MobEffectInstance(
						MobEffects.DIG_SPEED,
						20 * 20,
						1,
						false,
						false
				));
			}
		}
	}
}