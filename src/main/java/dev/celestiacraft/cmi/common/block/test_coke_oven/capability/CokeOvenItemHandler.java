package dev.celestiacraft.cmi.common.block.test_coke_oven.capability;

import dev.celestiacraft.cmi.common.block.test_coke_oven.TestCokeOvenBlockEntity;
import dev.celestiacraft.cmi.common.block.test_coke_oven.TestCokeOvenIOBlockEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;

public class CokeOvenItemHandler implements IItemHandler {
	private final CokeOvenItemCapability handler;
	private final TestCokeOvenBlockEntity entity;

	public CokeOvenItemHandler(CokeOvenItemCapability handler, TestCokeOvenIOBlockEntity entity) {
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
		// 只允许插入 input 槽
		if (!entity.isStructureValid() || slot != 0) {
			return stack;
		}
		return handler.insertItem(slot, stack, simulate);
	}

	@Override
	public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
		// 只允许从 output 槽取
		if (!entity.isStructureValid() || slot != 1) {
			return ItemStack.EMPTY;
		}
		return handler.extractItem(slot, amount, simulate);
	}

	@Override
	public int getSlotLimit(int slot) {
		return handler.getSlotLimit(slot);
	}

	@Override
	public boolean isItemValid(int slot, @NotNull ItemStack stack) {
		// ✔ 只允许 input
		return slot == 0;
	}
}