package dev.celestiacraft.cmi.common.block.test_multiblock;

import dev.celestiacraft.cmi.Cmi;
import dev.celestiacraft.cmi.common.register.CmiBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import dev.celestiacraft.libs.compat.patchouli.multiblock.*;
import org.jetbrains.annotations.NotNull;
import vazkii.patchouli.api.IMultiblock;

import javax.annotation.Nullable;

public class TestMultiblockBlockEntity extends BlockEntity implements IMultiblockProvider {
	public TestMultiblockBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
		super(type, pos, blockState);
	}

	private static final Lazy<IMultiblock> STRUCTURE = Lazy.of(() -> {
		return StructureBuilder.create(new String[][]{
						{
								"AAA",
								"AAA",
								"AAA"
						},
						{
								"A0A",
								"AAA",
								"AAA"
						},
						{
								"AAA",
								"AAA",
								"AAA"
						}
				})
				// 木板
				.define('A', (builder) -> {
					builder.block(Blocks.COBBLESTONE);
				})
				.define('0', (builder) -> {
					builder.block(CmiBlock.TEST_MULTIBLOCK.get());
				})
				.build();
	});

	private int energyStored = 0;

	private FluidStack fluid = FluidStack.EMPTY;

	private final CapabilityHandler capabilityHandler = new CapabilityHandler();

	private final MultiblockHandler MULTIBLOCK = MultiblockHandler.builder(this, STRUCTURE)
			.translationKey(String.format("multiblock.building.%s.test_multiblock", Cmi.MODID))
			.renderOffset(0, -1, 0)
			.cacheTicks(20)
			.build();

	@Override
	public MultiblockHandler getMultiblockHandler() {
		return MULTIBLOCK;
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
	public void load(CompoundTag tag) {
		super.load(tag);
		energyStored = tag.getInt("Energy");
		fluid = FluidStack.loadFluidStackFromNBT(tag.getCompound("Fluid"));
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
		private final ItemStackHandler itemHandler = new ItemStackHandler(1) {
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

		private final IFluidHandler fluidHandler = new IFluidHandler() {

			@Override
			public int getTanks() {
				return 1;
			}

			@Override
			public @NotNull FluidStack getFluidInTank(int tank) {
				return fluidHandler.getFluidInTank(tank);
			}

			@Override
			public int getTankCapacity(int tank) {
				return 32000;
			}

			@Override
			public boolean isFluidValid(int tank, @NotNull FluidStack stack) {
				if (isStructureValid()) {
					fluidHandler.getFluidInTank(tank);
					return stack.isFluidEqual(fluidHandler.getFluidInTank(tank));
				} else {
					return false;
				}
			}

			@Override
			public int fill(FluidStack stack, FluidAction action) {
				if (!isStructureValid() || !isFluidValid(0, stack)) {
					return 0;
				}
				if (level != null) {
					level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
				}
				int filled = Math.min(stack.getAmount(), 32000 - fluid.getAmount());
				fluid.setAmount(stack.getAmount() + filled);
				return filled;
			}

			@Override
			public @NotNull FluidStack drain(FluidStack stack, FluidAction action) {
				if (!isStructureValid() || !isFluidValid(0, stack)) {
					return FluidStack.EMPTY;
				}
				if (level != null) {
					level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
				}
				int drained = Math.min(stack.getAmount(), fluid.getAmount());
				fluid.setAmount(fluid.getAmount() - drained);
				return new FluidStack(fluid, drained);
			}

			@Override
			public @NotNull FluidStack drain(int maxDrain, FluidAction action) {
				if (!isStructureValid()) {
					return FluidStack.EMPTY;
				}
				if (level != null) {
					level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
				}
				int drained = Math.min(maxDrain, fluid.getAmount());
				return new FluidStack(fluid, drained);
			}
		};

		private final IEnergyStorage energyStorage = new IEnergyStorage() {
			@Override
			public int receiveEnergy(int maxReceive, boolean simulate) {
				int received = Math.min(1000 - energyStored, maxReceive);

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
				return true;
			}

			@Override
			public boolean canReceive() {
				return true;
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
