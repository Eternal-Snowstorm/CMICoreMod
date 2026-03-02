package dev.celestiacraft.cmi.common.item.mechanism;

import dev.celestiacraft.cmi.common.item.MechanismItem;
import dev.celestiacraft.cmi.common.register.CmiFoodBuilder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class PigIronItem extends MechanismItem {
	public PigIronItem(Properties properties) {
		super(properties.food(CmiFoodBuilder.PIG_IRON));
	}

	@Override
	public @NotNull ItemStack finishUsingItem(@NotNull ItemStack stack, @NotNull Level level, @NotNull LivingEntity entity) {
		ItemStack result = super.finishUsingItem(stack, level, entity);

		if (entity instanceof Player player) {
			if (!level.isClientSide() && !player.getAbilities().instabuild) {
				result.grow(1);
			}
		}

		return result;
	}
}