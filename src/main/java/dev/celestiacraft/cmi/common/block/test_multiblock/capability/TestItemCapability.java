package dev.celestiacraft.cmi.common.block.test_multiblock.capability;

import dev.celestiacraft.cmi.common.block.test_multiblock.TestMultiblockBlockEntity;
import net.minecraftforge.items.ItemStackHandler;

public class TestItemCapability extends ItemStackHandler {
	private final TestMultiblockBlockEntity entity;

	public TestItemCapability(TestMultiblockBlockEntity entity) {
		super(32);
		this.entity = entity;
	}

	@Override
	protected void onContentsChanged(int slot) {
		entity.setChanged();
	}
}