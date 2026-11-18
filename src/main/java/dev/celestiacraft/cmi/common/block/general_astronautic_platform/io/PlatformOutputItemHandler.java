package dev.celestiacraft.cmi.common.block.general_astronautic_platform.io;

import org.jetbrains.annotations.NotNull;

import dev.celestiacraft.cmi.common.block.general_astronautic_platform.GeneralAstronauticPlatformBlockEntity;
import dev.celestiacraft.cmi.common.entity.prospecting_rocket.ProspectingRocketTier;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

public class PlatformOutputItemHandler implements IItemHandler {
	private static final int MAX_SLOTS = ProspectingRocketTier.TIER_4.cargoSlots();

	private final GeneralAstronauticPlatformBlockEntity owner;

	public PlatformOutputItemHandler(GeneralAstronauticPlatformBlockEntity owner) {
		this.owner = owner;
	}

	@Override
	public int getSlots() {
		return MAX_SLOTS;
	}

	@Override
	public @NotNull ItemStack getStackInSlot(int slot) {
		IItemHandler handler = owner.getRocketItems();
		if (handler == null || slot < 0 || slot >= handler.getSlots()) {
			return ItemStack.EMPTY;
		}
		return handler.getStackInSlot(slot).copy();
	}

	// 输出端口不允许插入
	@Override
	public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
		return stack;
	}

	@Override
	public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
		IItemHandler handler = owner.getRocketItems();
		if (handler == null || slot < 0 || slot >= handler.getSlots()) {
			return ItemStack.EMPTY;
		}
		return handler.extractItem(slot, amount, simulate);
	}

	@Override
	public int getSlotLimit(int slot) {
		return 64;
	}

	@Override
	public boolean isItemValid(int slot, @NotNull ItemStack stack) {
		return false;
	}
}
