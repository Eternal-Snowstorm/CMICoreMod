package dev.celestiacraft.cmi.compat.create;

import com.simibubi.create.content.equipment.armor.BacktankUtil;
import com.simibubi.create.content.equipment.armor.DivingHelmetItem;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class CreateOxygenSupport {
	private CreateOxygenSupport() {
	}

	public static boolean hasBacktankSupport(LivingEntity entity) {
		ItemStack divingHelmet = DivingHelmetItem.getWornItem(entity);
		if (divingHelmet.isEmpty()) {
			return false;
		}

		List<ItemStack> backtanks = BacktankUtil.getAllWithAir(entity);
		return !backtanks.isEmpty();
	}

	public static void consumeBacktankAir(LivingEntity entity, float amount) {
		List<ItemStack> backtanks = BacktankUtil.getAllWithAir(entity);
		if (!backtanks.isEmpty()) {
			BacktankUtil.consumeAir(entity, backtanks.get(0), amount);
		}
	}
}
