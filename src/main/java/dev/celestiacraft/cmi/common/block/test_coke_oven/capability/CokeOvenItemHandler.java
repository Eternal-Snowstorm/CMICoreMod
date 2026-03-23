package dev.celestiacraft.cmi.common.block.test_coke_oven.capability;

import dev.celestiacraft.cmi.common.block.test_coke_oven.TestCokeOvenIOBlockEntity;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;

public class CokeOvenItemHandler implements IItemHandler {
	private final CokeOvenItemCapability handler;
	private final TestCokeOvenIOBlockEntity entity;

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
		// slot0：输入(限制)
		if (slot == 0) {
			return handler.insertItem(slot, stack, simulate);
		}

		// slot1：允许插入(给机器用)
		if (slot == 1) {
			return handler.insertItem(slot, stack, simulate);
		}

		return stack;
	}

	@Override
	public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
		// 只允许从 output 槽取
		if (slot != 1) {
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
		if (slot == 0) {
			return stack.is(ItemTags.LOGS);
		}
		return slot == 1; // ✅ 允许输出插入
	}
}