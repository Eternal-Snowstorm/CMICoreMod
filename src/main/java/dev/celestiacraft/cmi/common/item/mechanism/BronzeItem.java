package dev.celestiacraft.cmi.common.item.mechanism;

import dev.celestiacraft.cmi.common.item.MechanismItem;
import dev.celestiacraft.cmi.common.register.CmiMechanism;
import dev.celestiacraft.libs.compat.curios.ICuriosHelper;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.List;

public class BronzeItem extends MechanismItem {
	public BronzeItem(Properties properties) {
		super(properties);
	}

	@Override
	protected boolean useAfterConsume() {
		return false;
	}

	@Override
	protected InteractionResultHolder<ItemStack> onMechanismUse(Level level, Player player, InteractionHand hand) {
		ItemStack item = player.getItemInHand(hand);
		player.swing(hand);

		level.playSound(
				null,
				player.getX(),
				player.getY(),
				player.getZ(),
				SoundEvents.FIRE_EXTINGUISH,
				SoundSource.PLAYERS,
				1.0F,
				1.0F
		);

		if (!level.isClientSide()) {
			List<LivingEntity> entities = level.getEntitiesOfClass(
					LivingEntity.class,
					player.getBoundingBox().inflate(2)
			);
			entities.forEach((entity) -> {
				if (entity.distanceToSqr(player) <= 4 && !trigger(entity, player)) {
					entity.hurt(entity.damageSources().hotFloor(), 8.0F);
				}
			});
		}

		return InteractionResultHolder.sidedSuccess(item, level.isClientSide());
	}

	private boolean trigger(LivingEntity entity, Player player) {
		return entity.hasEffect(MobEffects.FIRE_RESISTANCE)
				|| ICuriosHelper.hasItem(entity, CmiMechanism.NETHER.get());
	}
}