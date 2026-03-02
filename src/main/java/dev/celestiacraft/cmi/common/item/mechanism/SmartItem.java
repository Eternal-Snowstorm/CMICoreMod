package dev.celestiacraft.cmi.common.item.mechanism;

import dev.celestiacraft.cmi.Cmi;
import dev.celestiacraft.cmi.common.item.MechanismItem;
import dev.celestiacraft.cmi.common.register.CmiMechanism;
import dev.celestiacraft.libs.compat.curios.CuriosUtils;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Cmi.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class SmartItem extends MechanismItem {
	public SmartItem(Properties properties) {
		super(properties);
	}

	@SubscribeEvent
	public static void onLivingHurt(LivingHurtEvent event) {
		if (!(event.getSource().getEntity() instanceof Player player)) {
			return;
		}

		if (!CuriosUtils.hasItem(player, CmiMechanism.SMART.get())) {
			return;
		}

		DamageSource source = event.getSource();

		// 判断是否是箭
		if (source.getDirectEntity() instanceof AbstractArrow) {
			LivingEntity target = event.getEntity();

			// 清除无敌帧
			target.invulnerableTime = 0;
		}
	}
}