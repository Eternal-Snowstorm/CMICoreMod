package dev.celestiacraft.cmi.common.block.test_coke_oven.capability;

import dev.celestiacraft.cmi.common.block.test_coke_oven.TestCokeOvenBlockEntity;
import net.minecraftforge.items.ItemStackHandler;

public class CokeOvenItemCapability extends ItemStackHandler {
	private final TestCokeOvenBlockEntity entity;

	public CokeOvenItemCapability(TestCokeOvenBlockEntity entity) {
		super(2);
		this.entity = entity;
	}

	@Override
	protected void onContentsChanged(int slot) {
		entity.setChanged();
	}
}