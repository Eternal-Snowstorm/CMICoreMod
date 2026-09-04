package dev.celestiacraft.cmi.common.item.mechanism;

import dev.celestiacraft.cmi.api.register.item.MechanismItem;

public class AntimatterItem extends MechanismItem {
	public AntimatterItem(Properties properties) {
		super(properties);
	}

	@Override
	protected boolean useAfterConsume() {
		return false;
	}
}