package top.nebula.cmi.compat.tconstruct.modifier;

import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.Tags;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import slimeknights.tconstruct.library.modifiers.Modifier;
import top.nebula.cmi.Cmi;
import top.nebula.utils.compat.tconstruct.util.SimpleTConUtils;

@SuppressWarnings("ALL")
@Mod.EventBusSubscriber(modid = Cmi.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class Acupoint extends Modifier {
	// 血量阈值
	private static final double LIFE_THRESHOLD = 0.25;
	// 触发概率
	private static final double TRIGGER_PROBABILITY = 0.25;

	@SubscribeEvent
	public static void onLivingHurt(LivingHurtEvent event) {
		LivingEntity entity = event.getEntity();
		DamageSource source = event.getSource();

		if (!(source.getEntity() instanceof Player player)) {
			return;
		}

		boolean hasModifier = SimpleTConUtils.hasModifier(
				player.getItemInHand(InteractionHand.MAIN_HAND),
				Cmi.loadResource("acupoint").toString()
		);
		boolean isBoss = entity.getType().is(Tags.EntityTypes.BOSSES);
		MutableComponent tranKey = Component.translatable("message.cmi.modifier.acupoint")
				.withStyle(ChatFormatting.RED)
				.withStyle(ChatFormatting.BOLD);

		if (hasModifier) {
			// 如果是 BOSS, 在血量低于 LIFE_THRESHOLD / 2 时攻击有 TRIGGER_PROBABILITY 的概率直接斩杀
			if (isBoss) {
				if (entity.getHealth() <= entity.getMaxHealth() * LIFE_THRESHOLD / 2) {
					if (Math.random() < TRIGGER_PROBABILITY) {
						player.displayClientMessage(tranKey, true);
						spawnSonicBoom(entity);
						event.setAmount(entity.getHealth());
					}
				}
			} else {
				// 如果不是 BOSS 则阈值不减半
				if (entity.getHealth() <= entity.getMaxHealth() * LIFE_THRESHOLD) {
					if (Math.random() < TRIGGER_PROBABILITY) {
						player.displayClientMessage(tranKey, true);
						spawnSonicBoom(entity);
						event.setAmount(entity.getHealth());
					}
				}
			}
		}
	}

	/**
	 * 触发音爆效果
	 *
	 * @param entity
	 */
	private static void spawnSonicBoom(LivingEntity entity) {
		if (!(entity.level() instanceof ServerLevel level)) {
			return;
		}

		level.sendParticles(
				ParticleTypes.SONIC_BOOM,
				entity.getX(),
				entity.getY() + entity.getBbHeight() * 0.5,
				entity.getZ(),
				1,
				0,
				0,
				0,
				0
		);

		level.playSound(
				null,
				entity.blockPosition(),
				SoundEvents.WARDEN_SONIC_BOOM,
				SoundSource.PLAYERS,
				1.0F,
				1.0F
		);
	}
}