package dev.celestiacraft.cmi.common.block.space_elevator_base_console.capability;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

public class ConsoleInputItemHandler implements IItemHandler {
	private final ItemStackHandler backing;

	public ConsoleInputItemHandler(ItemStackHandler backing) {
		this.backing = backing;
	}

	@Override
	public int getSlots() {
		return backing.getSlots();
	}

	@Override
	public @NotNull ItemStack getStackInSlot(int slot) {
		return backing.getStackInSlot(slot);
	}

	@Override
	public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
		return backing.insertItem(slot, stack, simulate);
	}

	@Override
	public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
		return ItemStack.EMPTY;
	}

	@Override
	public int getSlotLimit(int slot) {
		return backing.getSlotLimit(slot);
	}

	@Override
	public boolean isItemValid(int slot, @NotNull ItemStack stack) {
		return backing.isItemValid(slot, stack);
	}
}
