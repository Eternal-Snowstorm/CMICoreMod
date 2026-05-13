package dev.celestiacraft.cmi.common.block.space_elevator_base_console;

import dev.celestiacraft.cmi.common.block.space_elevator_base_console.capability.*;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

public class SpaceElevatorBaseConsoleBlockEntity extends BlockEntity implements GeoBlockEntity {
	public static final int ENERGY_CAPACITY = 10_000_000;
	public static final int ENERGY_MAX_RECEIVE = 50_000;
	public static final int LAUNCH_ENERGY_COST = 1_000_000;
	public static final int FLUID_TANK_CAPACITY = 16_000;
	public static final int ITEM_SLOT_COUNT = 9;

	public static final Direction INPUT_SIDE = Direction.EAST;
	public static final Direction OUTPUT_SIDE = Direction.WEST;
	public static final Direction ENERGY_SIDE = Direction.SOUTH;

	private static final RawAnimation IDLE_ANIM = RawAnimation.begin().thenLoop("idle");

	private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

	@Getter
    private int energyStored = 0;

	@Getter
    private final ItemStackHandler inputItems = new ItemStackHandler(ITEM_SLOT_COUNT) {
		@Override
		protected void onContentsChanged(int slot) {
			setChanged();
		}
	};
	@Getter
    private final ItemStackHandler outputItems = new ItemStackHandler(ITEM_SLOT_COUNT) {
		@Override
		protected void onContentsChanged(int slot) {
			setChanged();
		}
	};
	private final FluidTank inputFluid = new FluidTank(FLUID_TANK_CAPACITY) {
		@Override
		protected void onContentsChanged() {
			setChanged();
		}
	};
	@Getter
    private final FluidTank outputFluid = new FluidTank(FLUID_TANK_CAPACITY) {
		@Override
		protected void onContentsChanged() {
			setChanged();
		}
	};

	@Getter
    private LazyOptional<IEnergyStorage> energyCap = LazyOptional.empty();
	@Getter
    private LazyOptional<IItemHandler> inputItemCap = LazyOptional.empty();
	@Getter
    private LazyOptional<IItemHandler> outputItemCap = LazyOptional.empty();
	@Getter
    private LazyOptional<IFluidHandler> inputFluidCap = LazyOptional.empty();
	@Getter
    private LazyOptional<IFluidHandler> outputFluidCap = LazyOptional.empty();

	public SpaceElevatorBaseConsoleBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	@Override
	public void onLoad() {
		super.onLoad();
		rebuildCaps();
		if (level != null && !level.isClientSide()) {
			SpaceElevatorBaseConsoleBlock.ensureStructure(level, worldPosition);
		}
	}

	private void rebuildCaps() {
		this.energyCap = LazyOptional.of(() -> new ConsoleEnergyStorage(this));
		this.inputItemCap = LazyOptional.of(() -> new ConsoleInputItemHandler(inputItems));
		this.outputItemCap = LazyOptional.of(() -> new ConsoleOutputItemHandler(outputItems));
		this.inputFluidCap = LazyOptional.of(() -> new ConsoleInputFluidHandler(inputFluid));
		this.outputFluidCap = LazyOptional.of(() -> new ConsoleOutputFluidHandler(outputFluid));
	}

	@Override
	public void invalidateCaps() {
		super.invalidateCaps();
		energyCap.invalidate();
		inputItemCap.invalidate();
		outputItemCap.invalidate();
		inputFluidCap.invalidate();
		outputFluidCap.invalidate();
	}

	@Override
	public void reviveCaps() {
		super.reviveCaps();
		rebuildCaps();
	}

	@Override
	public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction direction) {
		if (capability == ForgeCapabilities.ENERGY) {
			if (direction == null || direction == ENERGY_SIDE) {
				return energyCap.cast();
			}
			return LazyOptional.empty();
		}
		if (capability == ForgeCapabilities.ITEM_HANDLER) {
			if (direction == INPUT_SIDE) {
				return inputItemCap.cast();
			}
			if (direction == OUTPUT_SIDE) {
				return outputItemCap.cast();
			}
			return LazyOptional.empty();
		}
		if (capability == ForgeCapabilities.FLUID_HANDLER) {
			if (direction == INPUT_SIDE) {
				return inputFluidCap.cast();
			}
			if (direction == OUTPUT_SIDE) {
				return outputFluidCap.cast();
			}
			return LazyOptional.empty();
		}
		return super.getCapability(capability, direction);
	}

    public int getEnergyCapacity() {
		return ENERGY_CAPACITY;
	}

	public int getMaxReceive() {
		return ENERGY_MAX_RECEIVE;
	}

	public void addEnergy(int amount) {
		this.energyStored = Math.min(ENERGY_CAPACITY, this.energyStored + amount);
		setChanged();
		if (level != null && !level.isClientSide()) {
			level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
		}
	}

	public boolean consumeEnergy(int amount) {
		if (this.energyStored < amount) {
			return false;
		}
		this.energyStored -= amount;
		setChanged();
		if (level != null && !level.isClientSide()) {
			level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
		}
		return true;
	}

	@Override
	protected void saveAdditional(@NotNull CompoundTag tag) {
		super.saveAdditional(tag);
		tag.putInt("Energy", energyStored);
		tag.put("InputItems", inputItems.serializeNBT());
		tag.put("OutputItems", outputItems.serializeNBT());
		tag.put("InputFluid", inputFluid.writeToNBT(new CompoundTag()));
		tag.put("OutputFluid", outputFluid.writeToNBT(new CompoundTag()));
	}

	@Override
	public void load(@NotNull CompoundTag tag) {
		super.load(tag);
		energyStored = tag.getInt("Energy");
		inputItems.deserializeNBT(tag.getCompound("InputItems"));
		outputItems.deserializeNBT(tag.getCompound("OutputItems"));
		inputFluid.readFromNBT(tag.getCompound("InputFluid"));
		outputFluid.readFromNBT(tag.getCompound("OutputFluid"));
	}

	@Override
	public @NotNull CompoundTag getUpdateTag() {
		return saveWithoutMetadata();
	}

	@Override
	public @Nullable ClientboundBlockEntityDataPacket getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}

	@Override
	public @NotNull AABB getRenderBoundingBox() {
		return new AABB(worldPosition).inflate(6.0D, 4.0D, 6.0D);
	}

	@Override
	public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
		controllers.add(new AnimationController<>(this, "controller", 0, state -> {
			state.getController().setAnimation(IDLE_ANIM);
			return PlayState.CONTINUE;
		}).triggerableAnim("idle", IDLE_ANIM));
	}

	@Override
	public AnimatableInstanceCache getAnimatableInstanceCache() {
		return this.cache;
	}
}
