package dev.celestiacraft.cmi.common.block.test_coke_oven;

import blusunrize.immersiveengineering.common.fluids.IEFluid;
import blusunrize.immersiveengineering.common.register.IEFluids;
import dev.celestiacraft.cmi.Cmi;
import dev.celestiacraft.cmi.api.register.multiblock.ControllerBlockEntity;
import dev.celestiacraft.cmi.common.register.CmiMultiblock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidActionResult;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

public class TestCokeOvenBlockEntity extends ControllerBlockEntity {

	public TestCokeOvenBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state, CmiMultiblock.TEST_COKE_OVEN);
	}

	private final CapabilityHandler capabilityHandler = new CapabilityHandler();

	private FluidStack fluid = FluidStack.EMPTY;
	private int workTimer = 0;

	public static void tick(Level level, BlockPos pos, BlockState state, TestCokeOvenBlockEntity entity) {
		if (level.isClientSide()) {
			return;
		}

		entity.runRecipe();
	}

	private void runRecipe() {
		if (level == null || level.isClientSide()) {
			return;
		}

		ItemStack input = capabilityHandler.itemHandler.getStackInSlot(0);
		ItemStack output = capabilityHandler.itemHandler.getStackInSlot(1);
		int timeToWork = 20;

		boolean canWork = isStructureValid() && input.is(ItemTags.LOGS) && output.getCount() < 64;

		if (!canWork) {
			workTimer = 0;
			return;
		}

		workTimer++;

		setChanged();

		if (workTimer >= timeToWork) {
			workTimer = 0;

			input.shrink(1);
			capabilityHandler.itemHandler.insertItem(1, Items.CHARCOAL.getDefaultInstance(), false);
			capabilityHandler.fluidHandler.fill(new FluidStack(IEFluids.CREOSOTE.getStill(), 125), IFluidHandler.FluidAction.EXECUTE);
		}


	}

	@Override
	protected String getMultiblockKey() {
		return String.format("multiblock.building.%s.test_coke_oven", Cmi.MODID);
	}

	@Override
	public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, Direction direction) {
		if (capability == ForgeCapabilities.ITEM_HANDLER) {
			// 结构不全不给输入输出
			if (isStructureValid()) {
				return capabilityHandler.itemCapability.cast();
			}
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
		tag.put("Inventory", capabilityHandler.itemHandler.serializeNBT());
		tag.putInt("Fluid", fluid.getAmount());
	}

	@Override
	public void load(@NotNull CompoundTag tag) {
		super.load(tag);
		capabilityHandler.itemHandler.deserializeNBT(tag.getCompound("Inventory"));
		tag.put("Fluid", fluid.writeToNBT(new CompoundTag()));
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

		// 物品
		private final ItemStackHandler itemHandler = new ItemStackHandler(2) {
			@Override
			protected void onContentsChanged(int slot) {
				setChanged();
			}
		};

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
						return itemHandler.insertItem(0, stack, simulate);
					} else {
						return stack;
					}
				}

				@Override
				public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
					if (isStructureValid()) {
						return itemHandler.extractItem(1, amount, simulate);
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
				return 4000;
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

				int fillable = Math.min(stack.getAmount(), 4000 - fluid.getAmount());
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

		private final LazyOptional<IFluidHandler> fluidCapability = LazyOptional.of(() -> {
			return fluidHandler;
		});

		private void invalidate() {
			itemCapability.invalidate();
			fluidCapability.invalidate();
		}
	}
}
