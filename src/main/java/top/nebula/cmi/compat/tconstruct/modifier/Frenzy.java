package top.nebula.cmi.compat.tconstruct.modifier;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import slimeknights.tconstruct.library.modifiers.Modifier;
import top.nebula.cmi.Cmi;
import top.nebula.utils.compat.tconstruct.util.SimpleTConUtils;

@SuppressWarnings("ALL")
@Mod.EventBusSubscriber(modid = Cmi.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class Frenzy extends Modifier {
	@SubscribeEvent
	public static void onLivingHurt(LivingHurtEvent event) {
		LivingEntity entity = event.getEntity();
		DamageSource source = event.getSource();

		if (!(source.getEntity() instanceof Player player)) {
			return;
		}

		boolean hasModifier = SimpleTConUtils.hasModifier(
				player.getItemInHand(InteractionHand.MAIN_HAND),
				Cmi.loadResource("frenzy").toString()
		);

		if (!player.hasEffect(MobEffects.MOVEMENT_SPEED) || !hasModifier) {
			return;
		}

		// 获取速度等级
		int speedLevel = player.getEffect(MobEffects.MOVEMENT_SPEED).getAmplifier() + 1;

		// 指数型回血百分比(可以调整 exponent 来控制增长速率)
		// 指数因子, >1表示高等级回血更夸张
		double exponent = 1.5;
		double baseMin = 0.05;
		double baseMax = 0.10;

		// 根据等级指数放大
		double minPercent = Math.pow(speedLevel, exponent);
		double maxPercent = baseMax * Math.pow(speedLevel, exponent);

		double replyValue = event.getAmount() * (minPercent + Math.random() * (maxPercent - minPercent));
		// 保留两位小数
		replyValue = Math.round(replyValue * 100) / 100;

		player.setHealth((float) (player.getHealth() + replyValue));
	}
}