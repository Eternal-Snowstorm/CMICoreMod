package dev.celestiacraft.cmi.common.item.mechanism;

import dev.celestiacraft.cmi.common.item.MechanismItem;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;

public class CopperItem extends MechanismItem {
	public CopperItem(Properties properties) {
		super(properties);
	}

	@Override
	public boolean useAfterConsume() {
		return false;
	}

	@Override
	protected InteractionResultHolder<ItemStack> onMechanismUse(Level level, Player player, InteractionHand hand) {
		ThrownPotion potion = new ThrownPotion(level, player);
		ItemStack potionStack = Items.SPLASH_POTION.getDefaultInstance();

		PotionUtils.setPotion(potionStack, Potions.WATER);
		potion.setItem(potionStack);

		potion.shootFromRotation(
				player,
				player.getXRot(),
				player.getYRot(),
				0.0F,
				1.0F,
				0.0F
		);

		if (!level.isClientSide()) {
			level.addFreshEntity(potion);
		}

		level.playSound(
				null,
				player.getX(),
				player.getY(),
				player.getZ(),
				SoundEvents.ARROW_SHOOT,
				SoundSource.PLAYERS,
				0.5F,
				0.3F
		);

		return InteractionResultHolder.success(player.getItemInHand(hand));
	}
}