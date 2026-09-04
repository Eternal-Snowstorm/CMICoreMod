package dev.celestiacraft.cmi.common.item.mechanism;

import dev.celestiacraft.cmi.api.register.item.MechanismItem;
import dev.celestiacraft.cmi.common.register.CmiFoodBuilder;

public class PigIronItem extends MechanismItem {
	public PigIronItem(Properties properties) {
		super(properties.food(CmiFoodBuilder.PIG_IRON));
	}

	@Override
	protected boolean useAfterConsume() {
		return false;
	}
}