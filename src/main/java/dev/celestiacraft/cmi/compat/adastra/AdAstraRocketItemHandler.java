package dev.celestiacraft.cmi.compat.adastra;

import org.jetbrains.annotations.NotNull;

import earth.terrarium.adastra.common.entities.vehicles.Rocket;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;

public class AdAstraRocketItemHandler implements IItemHandler {
	private final IItemHandler backing;

	public AdAstraRocketItemHandler(@NotNull Rocket rocket) {
		this.backing = new InvWrapper(rocket.inventory());
	}

	@Override
	public int getSlots() {
		return backing.getSlots();
	}

	@Override
	public @NotNull ItemStack getStackInSlot(int slot) {
		if (slot < 0 || slot >= backing.getSlots()) {
			return ItemStack.EMPTY;
		}
		return backing.getStackInSlot(slot).copy();
	}

	@Override
	public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
		if (slot < 0 || slot >= backing.getSlots()) {
			return stack;
		}
		return backing.insertItem(slot, stack, simulate);
	}

	@Override
	public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
		if (slot < 0 || slot >= backing.getSlots()) {
			return ItemStack.EMPTY;
		}
		return backing.extractItem(slot, amount, simulate);
	}

	@Override
	public int getSlotLimit(int slot) {
		return 64;
	}

	@Override
	public boolean isItemValid(int slot, @NotNull ItemStack stack) {
		if (slot < 0 || slot >= backing.getSlots()) {
			return false;
		}
		return backing.isItemValid(slot, stack);
	}
}
