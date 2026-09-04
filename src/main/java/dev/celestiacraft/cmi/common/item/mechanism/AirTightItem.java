package dev.celestiacraft.cmi.common.item.mechanism;

import dev.celestiacraft.cmi.api.register.item.MechanismItem;

public class AirTightItem extends MechanismItem {
	public AirTightItem(Properties properties) {
		super(properties);
	}

	@Override
	protected boolean useAfterConsume() {
		return false;
	}
}