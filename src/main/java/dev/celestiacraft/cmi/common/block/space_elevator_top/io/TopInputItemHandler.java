package dev.celestiacraft.cmi.common.block.space_elevator_top.io;

import org.jetbrains.annotations.NotNull;

import dev.celestiacraft.cmi.common.block.space_elevator_top.SpaceElevatorTopBlockEntity;
import dev.celestiacraft.cmi.common.entity.space_elevator.SpaceElevatorEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.IItemHandler;

public class TopInputItemHandler implements IItemHandler {
	private final SpaceElevatorTopBlockEntity owner;

	public TopInputItemHandler(SpaceElevatorTopBlockEntity owner) {
		this.owner = owner;
	}

	// 取锚定电梯货舱的物品能力，没有电梯时返回空
	private IItemHandler target() {
		SpaceElevatorEntity elevator = owner.findElevator();
		if (elevator == null) {
			return null;
		}
		return elevator.getCapability(ForgeCapabilities.ITEM_HANDLER).orElse(null);
	}

	@Override
	public int getSlots() {
		return SpaceElevatorEntity.CARGO_ITEM_SLOTS;
	}

	@Override
	public @NotNull ItemStack getStackInSlot(int slot) {
		IItemHandler handler = target();
		if (handler == null || slot < 0 || slot >= handler.getSlots()) {
			return ItemStack.EMPTY;
		}
		return handler.getStackInSlot(slot).copy();
	}

	@Override
	public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
		IItemHandler handler = target();
		if (handler == null || slot < 0 || slot >= handler.getSlots()) {
			return stack;
		}
		return handler.insertItem(slot, stack, simulate);
	}

	// 输入端口不允许抽取
	@Override
	public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
		return ItemStack.EMPTY;
	}

	@Override
	public int getSlotLimit(int slot) {
		return 64;
	}

	@Override
	public boolean isItemValid(int slot, @NotNull ItemStack stack) {
		IItemHandler handler = target();
		if (handler == null || slot < 0 || slot >= handler.getSlots()) {
			return false;
		}
		return handler.isItemValid(slot, stack);
	}
}