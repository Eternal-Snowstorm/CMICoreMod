package dev.celestiacraft.cmi.common.block.test_multiblock;

import dev.celestiacraft.cmi.Cmi;
import dev.celestiacraft.cmi.api.register.multiblock.MultiblockControllerBlockEntity;
import dev.celestiacraft.cmi.common.register.CmiMultiblock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import dev.celestiacraft.libs.compat.patchouli.multiblock.*;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class TestMultiblockBlockEntity extends MultiblockControllerBlockEntity {
	public TestMultiblockBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state, CmiMultiblock.TEST_MULTIBLOCK);
	}

	private int energyStored = 0;
	private FluidStack fluid = FluidStack.EMPTY;
	private final CapabilityHandler capabilityHandler = new CapabilityHandler();

	@Override
	protected String getMultiblockKey() {
		return String.format("multiblock.building.%s.test_multiblock", Cmi.MODID);
	}

	@Override
	protected int getRenderOffsetY() {
		return -1;
	}

	@Override
	public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction direction) {
		if (capability == ForgeCapabilities.ITEM_HANDLER) {
			// 结构不全不给输入输出
			if (!isStructureValid()) {
				return LazyOptional.empty();
			}
			return capabilityHandler.itemCapability.cast();
		}

		if (capability == ForgeCapabilities.ENERGY) {
			return capabilityHandler.energyCap.cast();
		}

		if (capability == ForgeCapabilities.FLUID_HANDLER) {
			return capabilityHandler.fluidCapability.cast();
		}

		return super.getCapability(capability, direction);
	}

	@Override
	public void invalidateCaps() {
		super.invalidateCaps();
		capabilityHandler.invalidate();
	}

	@Override
	protected void saveAdditional(@NotNull CompoundTag tag) {
		super.saveAdditional(tag);
		tag.putInt("Energy", energyStored);
		tag.putInt("Fluid", fluid.getAmount());
		tag.put("Inventory", capabilityHandler.itemHandler.serializeNBT());
	}

	@Override
	public void load(@NotNull CompoundTag tag) {
		super.load(tag);
		energyStored = tag.getInt("Energy");
		tag.put("Fluid", fluid.writeToNBT(new CompoundTag()));
		capabilityHandler.itemHandler.deserializeNBT(tag.getCompound("Inventory"));
	}

	@Override
	public @NotNull CompoundTag getUpdateTag() {
		return saveWithoutMetadata();
	}

	@Override
	public ClientboundBlockEntityDataPacket getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}

	private class CapabilityHandler {
		private final ItemStackHandler itemHandler = new ItemStackHandler(32) {
			@Override
			protected void onContentsChanged(int slot) {
				setChanged();
			}
		};

		// 物品
		private final LazyOptional<IItemHandler> itemCapability = LazyOptional.of(() -> {
			return new IItemHandler() {
				@Override
				public int getSlots() {
					return itemHandler.getSlots();
				}

				@Override
				public @NotNull ItemStack getStackInSlot(int slot) {
					return itemHandler.getStackInSlot(slot);
				}

				@Override
				public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
					if (isStructureValid()) {
						return itemHandler.insertItem(slot, stack, simulate);
					} else {
						return stack;
					}
				}

				@Override
				public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
					if (isStructureValid()) {
						return itemHandler.extractItem(slot, amount, simulate);
					} else {
						return ItemStack.EMPTY;
					}
				}

				@Override
				public int getSlotLimit(int slot) {
					return itemHandler.getSlotLimit(slot);
				}

				@Override
				public boolean isItemValid(int slot, @NotNull ItemStack stack) {
					return false;
				}
			};
		});

		// 流体
		private final IFluidHandler fluidHandler = new IFluidHandler() {
			@Override
			public int getTanks() {
				return 1;
			}

			@Override
			public @NotNull FluidStack getFluidInTank(int tank) {
				if (isStructureValid()) {
					return fluid.copy();
				} else {
					return FluidStack.EMPTY;
				}
			}

			@Override
			public int getTankCapacity(int tank) {
				return 32000;
			}

			@Override
			public boolean isFluidValid(int tank, @NotNull FluidStack stack) {
				return isStructureValid() && (fluid.isEmpty() || stack.isFluidEqual(fluid));
			}

			@Override
			public int fill(FluidStack stack, FluidAction action) {
				if (!isStructureValid() || stack.isEmpty()) {
					return 0;
				}
				if (!isFluidValid(0, stack)) {
					return 0;
				}

				int fillable = Math.min(stack.getAmount(), 32000 - fluid.getAmount());
				if (fillable <= 0) {
					return 0;
				}

				if (action == FluidAction.EXECUTE) {
					if (fluid.isEmpty()) {
						fluid = new FluidStack(stack, fillable);
					} else {
						fluid.grow(fillable);
					}

					setChanged();
					if (level != null) {
						level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
					}
				}

				return fillable;
			}

			@Override
			public @NotNull FluidStack drain(FluidStack stack, FluidAction action) {
				if (!isStructureValid() || stack.isEmpty()) {
					return FluidStack.EMPTY;
				}
				if (!stack.isFluidEqual(fluid)) {
					return FluidStack.EMPTY;
				}

				return drain(stack.getAmount(), action);
			}

			@Override
			public @NotNull FluidStack drain(int maxDrain, FluidAction action) {
				if (!isStructureValid() || maxDrain <= 0 || fluid.isEmpty()) {
					return FluidStack.EMPTY;
				}

				int drained = Math.min(maxDrain, fluid.getAmount());
				FluidStack result = new FluidStack(fluid, drained);

				if (action == FluidAction.EXECUTE) {
					fluid.shrink(drained);
					if (fluid.getAmount() <= 0) {
						fluid = FluidStack.EMPTY;
					}

					setChanged();
					if (level != null) {
						level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
					}
				}

				return result;
			}
		};

		// 能量
		private final IEnergyStorage energyStorage = new IEnergyStorage() {
			@Override
			public int receiveEnergy(int maxReceive, boolean simulate) {
				int received = Math.min(32000 - energyStored, maxReceive);

				if (!simulate && received > 0) {
					energyStored += received;
					setChanged();
					if (level != null) {
						level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
					}
				}

				return received;
			}

			@Override
			public int extractEnergy(int maxExtract, boolean simulate) {
				int extracted = Math.min(energyStored, maxExtract);

				if (!simulate && extracted > 0) {
					energyStored -= extracted;
					if (level != null) {
						level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
					}
				}

				return extracted;
			}

			@Override
			public int getEnergyStored() {
				return energyStored;
			}

			@Override
			public int getMaxEnergyStored() {
				return 32000;
			}

			@Override
			public boolean canExtract() {
				return isStructureValid();
			}

			@Override
			public boolean canReceive() {
				return isStructureValid();
			}
		};

		private final LazyOptional<IFluidHandler> fluidCapability = LazyOptional.of(() -> {
			return fluidHandler;
		});

		private final LazyOptional<IEnergyStorage> energyCap = LazyOptional.of(() -> {
			return energyStorage;
		});

		private void invalidate() {
			itemCapability.invalidate();
			fluidCapability.invalidate();
			energyCap.invalidate();
		}
	}
}
