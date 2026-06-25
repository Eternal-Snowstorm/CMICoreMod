package dev.celestiacraft.cmi.common.item.mechanism;

import com.simibubi.create.AllSoundEvents;
import dev.celestiacraft.cmi.common.item.MechanismItem;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

import java.util.List;

public class PotionItem extends MechanismItem {
	public PotionItem(Properties properties) {
		super(properties);
	}

	@Override
	protected InteractionResult onMechanismUseOn(UseOnContext context) {
		Player player = context.getPlayer();
		Level level = context.getLevel();
		ItemStack stack = context.getItemInHand();
		BlockPos pos = player.blockPosition();

		if (level.isClientSide()) {
			return InteractionResult.PASS;
		}

		if (stack.getItem() instanceof PotionItem item) {
			List<MobEffect> effects = List.of(
					MobEffects.SATURATION,
					MobEffects.FIRE_RESISTANCE,
					MobEffects.DIG_SPEED,
					MobEffects.DAMAGE_RESISTANCE,
					MobEffects.ABSORPTION,
					MobEffects.REGENERATION
			);
			effects.forEach((effect) -> {
				MobEffectInstance instance = new MobEffectInstance(effect, 300, 8);
				player.addEffect(instance);
			});
			player.playNotifySound(AllSoundEvents.CRAFTER_CRAFT.getMainEvent(), SoundSource.VOICE, 2, 1);
			player.swing(InteractionHand.MAIN_HAND, true);
		}

		return InteractionResult.SUCCESS;
	}
}