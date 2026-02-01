package top.nebula.cmi.common.block.void_dust_collector;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import top.nebula.cmi.config.CommonConfig;

import javax.annotation.Nullable;

public class VoidDustCollectorBlockEnitiy extends BlockEntity {
	private static final int CAPACITY = CommonConfig.VOID_DUST_COLLECTOR_ENERGY_CAPACITY.get();
	private static final int MAX_RECEIVE = CommonConfig.VOID_DUST_COLLECTOR_MAX_RECEIVE.get();
	private static final int ENERGY_CONSUMPTION = CommonConfig.VOID_DUST_COLLECTOR_ENERGY_CONSUMPTION.get();
	private static final int WORK_HEIGHT = CommonConfig.VOID_DUST_COLLECTOR_WORK_HEIGHT.get();
	private static final int WORK_TIME = CommonConfig.VOID_DUST_COLLECTOR_WORK_TIME.get();
	private static final Block BLOCKS_BELOW = Blocks.GOLD_BLOCK;
	private static final ItemStack OUTPUT_ITEM = Items.DIAMOND.getDefaultInstance();

	private int energyStored = 0;
	private int workTimer = 0;
	private int workTimeRequired = 0;

	private final ForgeCapabilityHandler capabilityHandler = new ForgeCapabilityHandler();

	public VoidDustCollectorBlockEnitiy(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	public static void tick(Level level, BlockPos pos, BlockState state, VoidDustCollectorBlockEnitiy enitiy) {
		if (level.isClientSide()) {
			return;
		}

		enitiy.serverTick();
	}

	private void serverTick() {
		if (level == null || level.isClientSide()) {
			return;
		}

		ItemStack stack = capabilityHandler.itemHandler.getStackInSlot(0);

		boolean canWork = energyStored >= 1000 &&
				level.getBlockState(worldPosition.below()).is(BLOCKS_BELOW) &&
				this.getBlockPos().getY() == WORK_HEIGHT &&
				(stack.isEmpty() || stack.getCount() < stack.getMaxStackSize());

		// 同步BlockState
		BlockState state = getBlockState();
		boolean wasWorking = state.getValue(VoidDustCollectorBlock.WORKING);

		if (wasWorking != canWork) {
			level.setBlock(
					worldPosition,
					state.setValue(VoidDustCollectorBlock.WORKING, canWork),
					3
			);
		}

		if (!canWork) {
			workTimer = 0;
			workTimeRequired = 0;
			return;
		}

		// 初始化随机工作时间
		if (workTimeRequired <= 0) {
			workTimeRequired = WORK_TIME;
			workTimer = 0;
		}

		// 每 tick 消耗能量
		energyStored -= ENERGY_CONSUMPTION;
		workTimer++;

		setChanged();

		// 完成一次生成
		if (workTimer >= workTimeRequired) {
			if (stack.isEmpty()) {
				capabilityHandler.itemHandler.setStackInSlot(0, OUTPUT_ITEM);
			} else {
				stack.grow(1);
			}

			workTimer = 0;
			workTimeRequired = 0;
			setChanged();
		}
	}

	public boolean isWorking() {
		if (level == null) {
			return false;
		}

		if (energyStored < 1000) {
			return false;
		}

		BlockPos below = worldPosition.below();
		if (!level.getBlockState(below).is(Blocks.GOLD_BLOCK)) {
			return false;
		}

		return capabilityHandler.itemHandler.getStackInSlot(0).isEmpty();
	}

	@Override
	public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction direction) {
		if (capability == ForgeCapabilities.ITEM_HANDLER) {
			// 上方不给抽
			if (direction == Direction.UP) {
				return LazyOptional.empty();
			}
			return capabilityHandler.itemCapability.cast();
		}

		if (capability == ForgeCapabilities.ENERGY) {
			return capabilityHandler.energyCap.cast();
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
		tag.putInt("WorkTimer", workTimer);
		tag.putInt("WorkTimeRequired", workTimeRequired);
		tag.put("Inventory", capabilityHandler.itemHandler.serializeNBT());
	}

	@Override
	public @NotNull CompoundTag getUpdateTag() {
		return saveWithoutMetadata();
	}

	@Override
	@Nullable
	public ClientboundBlockEntityDataPacket getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}

	@Override
	public void load(@NotNull CompoundTag tag) {
		super.load(tag);
		energyStored = tag.getInt("Energy");
		workTimer = tag.getInt("WorkTimer");
		workTimeRequired = tag.getInt("WorkTimeRequired");
		capabilityHandler.itemHandler.deserializeNBT(tag.getCompound("Inventory"));
	}

	private class ForgeCapabilityHandler {
		// 物品
		private final ItemStackHandler itemHandler = new ItemStackHandler(1) {
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
					return stack;
				}

				@Override
				public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
					return itemHandler.extractItem(slot, amount, simulate);
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

		// 能量
		private final IEnergyStorage energyHandler = new IEnergyStorage() {
			@Override
			public int receiveEnergy(int maxReceive, boolean simulate) {
				int received = Math.min(CAPACITY - energyStored, Math.min(MAX_RECEIVE, maxReceive));

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
				return 0;
			}

			@Override
			public int getEnergyStored() {
				return energyStored;
			}

			@Override
			public int getMaxEnergyStored() {
				return CAPACITY;
			}

			@Override
			public boolean canExtract() {
				return false;
			}

			@Override
			public boolean canReceive() {
				return true;
			}
		};

		private final LazyOptional<IEnergyStorage> energyCap = LazyOptional.of(() -> {
			return energyHandler;
		});

		private void invalidate() {
			itemCapability.invalidate();
			energyCap.invalidate();
		}
	}
}