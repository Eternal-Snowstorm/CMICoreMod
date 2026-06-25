package dev.celestiacraft.cmi.common.item.mechanism;

import com.simibubi.create.AllItems;
import dev.celestiacraft.cmi.common.item.MechanismItem;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.UseOnContext;

public class WoodenItem extends MechanismItem {
	public WoodenItem(Properties properties) {
		super(properties);
	}

	@Override
	public boolean useAfterConsume() {
		return false;
	}

	@Override
	protected InteractionResult onMechanismUseOn(UseOnContext context) {
		return useOtherItem(AllItems.TREE_FERTILIZER.get(), context);
	}

	@Override
	protected int getCooldownTicks() {
		return 5 + RANDOM.nextInt(6);
	}
}