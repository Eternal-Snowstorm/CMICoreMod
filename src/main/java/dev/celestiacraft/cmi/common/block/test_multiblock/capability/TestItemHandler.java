package dev.celestiacraft.cmi.common.block.test_multiblock.capability;

import dev.celestiacraft.cmi.common.block.test_multiblock.TestMultiblockBlockEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;

public class TestItemHandler implements IItemHandler {
	private final TestItemCapability handler;
	private final TestMultiblockBlockEntity entity;

	public TestItemHandler(TestItemCapability handler, TestMultiblockBlockEntity entity) {
		this.handler = handler;
		this.entity = entity;
	}

	@Override
	public int getSlots() {
		return handler.getSlots();
	}

	@Override
	public @NotNull ItemStack getStackInSlot(int slot) {
		return handler.getStackInSlot(slot);
	}

	@Override
	public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
		return entity.isStructureValid()
				? handler.insertItem(slot, stack, simulate)
				: stack;
	}

	@Override
	public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
		return entity.isStructureValid()
				? handler.extractItem(slot, amount, simulate)
				: ItemStack.EMPTY;
	}

	@Override
	public int getSlotLimit(int slot) {
		return handler.getSlotLimit(slot);
	}

	@Override
	public boolean isItemValid(int slot, @NotNull ItemStack stack) {
		return false;
	}
}