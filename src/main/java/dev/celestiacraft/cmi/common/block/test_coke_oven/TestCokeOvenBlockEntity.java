package dev.celestiacraft.cmi.common.block.test_coke_oven;

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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

public class TestCokeOvenBlockEntity extends ControllerBlockEntity {

	public TestCokeOvenBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state, CmiMultiblock.TEST_COKE_OVEN);
	}

	private final CapabilityHandler capabilityHandler = new CapabilityHandler();

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

		ItemStack input = capabilityHandler.inputHandler.getStackInSlot(0);
		ItemStack output = capabilityHandler.outputHandler.getStackInSlot(0);
		int timeToWork = 20;

		boolean canWork = isStructureValid() && input.is(ItemTags.LOGS) && output.getCount() < 64;

		if (!canWork) {
			workTimer = 0;
		} else {
			if (workTimer >= timeToWork) {
				workTimer = 0;

				input.shrink(1);
				capabilityHandler.outputHandler.insertItem(0, Items.CHARCOAL.getDefaultInstance(), false);
			} else {
				workTimer++;
			}
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
			if (!isStructureValid()) {
				return capabilityHandler.outputCapability.cast();
			} else {
				return capabilityHandler.inputCapability.cast();
			}
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
		tag.put("Input", capabilityHandler.inputHandler.serializeNBT());
		tag.put("Output", capabilityHandler.outputHandler.serializeNBT());
	}

	@Override
	public void load(@NotNull CompoundTag tag) {
		super.load(tag);
		capabilityHandler.inputHandler.deserializeNBT(tag.getCompound("Input"));
		capabilityHandler.outputHandler.deserializeNBT(tag.getCompound("Output"));
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
		private final ItemStackHandler inputHandler = new ItemStackHandler(1) {
			@Override
			protected void onContentsChanged(int slot) {
				setChanged();
			}
		};

		private final ItemStackHandler outputHandler = new ItemStackHandler(1) {
			@Override
			protected void onContentsChanged(int slot) {
				setChanged();
			}
		};

		private final LazyOptional<IItemHandler> inputCapability = LazyOptional.of(() -> {
			return new IItemHandler() {
				@Override
				public int getSlots() {
					return inputHandler.getSlots();
				}

				@Override
				public @NotNull ItemStack getStackInSlot(int slot) {
					return inputHandler.getStackInSlot(slot);
				}

				@Override
				public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
					if (isStructureValid()) {
						return inputHandler.insertItem(slot, stack, simulate);
					} else {
						return stack;
					}
				}

				@Override
				public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
					return ItemStack.EMPTY;
				}

				@Override
				public int getSlotLimit(int slot) {
					return inputHandler.getSlotLimit(slot);
				}

				@Override
				public boolean isItemValid(int slot, @NotNull ItemStack stack) {
					return false;
				}
			};
		});

		private final LazyOptional<IItemHandler> outputCapability = LazyOptional.of(() -> {
			return new IItemHandler() {
				@Override
				public int getSlots() {
					return outputHandler.getSlots();
				}

				@Override
				public @NotNull ItemStack getStackInSlot(int slot) {
					return outputHandler.getStackInSlot(slot);
				}

				@Override
				public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
					return stack;
				}

				@Override
				public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
					if (isStructureValid()) {
						return outputHandler.extractItem(slot, amount, simulate);
					} else {
						return ItemStack.EMPTY;
					}
				}

				@Override
				public int getSlotLimit(int slot) {
					return outputHandler.getSlotLimit(slot);
				}

				@Override
				public boolean isItemValid(int slot, @NotNull ItemStack stack) {
					return false;
				}
			};
		});

		private void invalidate() {
			inputCapability.invalidate();
			outputCapability.invalidate();
		}
	}
}
