package dev.celestiacraft.cmi.common.block.space_elevator_base_console;

import dev.celestiacraft.cmi.common.block.space_elevator_base_console.capability.*;
import dev.celestiacraft.cmi.common.entity.space_elevator.SpaceElevatorEntity;
import dev.celestiacraft.cmi.common.recipe.space_elevator_construction.SpaceElevatorConstructionRecipe;
import dev.celestiacraft.cmi.compat.adastra.SpaceElevatorConstructionHandler;
import dev.celestiacraft.cmi.compat.adastra.SpaceElevatorMaterialStorage;
import dev.celestiacraft.cmi.network.CmiNetwork;
import dev.celestiacraft.cmi.network.s2c.SyncSpaceElevatorMaterialsPacket;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

public class SpaceElevatorBaseConsoleBlockEntity extends BlockEntity implements GeoBlockEntity, dev.celestiacraft.cmi.common.entity.space_elevator.ElevatorEnergyAnchor {
	public static final int ENERGY_CAPACITY = 10_000_000;
	public static final int ENERGY_MAX_RECEIVE = 50_000;
	public static final int LAUNCH_ENERGY_COST = 1_000_000;
	public static final int FLUID_TANK_CAPACITY = 16_000;
	public static final int ITEM_SLOT_COUNT = 9;

	public static final Direction INPUT_SIDE = Direction.EAST;
	public static final Direction OUTPUT_SIDE = Direction.WEST;
	public static final Direction ENERGY_SIDE = Direction.SOUTH;

	private static final int INPUT_PULL_INTERVAL_TICKS = 20;
	private static final double MATERIAL_SYNC_RADIUS = 32.0D;

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
	public int getLaunchEnergyCost() {
		return LAUNCH_ENERGY_COST;
	}

	@Override
	public boolean consumeLaunchEnergy() {
		return consumeEnergy(LAUNCH_ENERGY_COST);
	}

	public static void serverTick(Level level, BlockPos pos, BlockState state, SpaceElevatorBaseConsoleBlockEntity entity) {
		if (!(level instanceof ServerLevel serverLevel)) {
			return;
		}
		if ((serverLevel.getGameTime() + pos.asLong()) % INPUT_PULL_INTERVAL_TICKS != 0) {
			return;
		}
		SpaceElevatorEntity elevator = SpaceElevatorConstructionHandler.getNearbyElevator(serverLevel, pos);
		if (elevator == null) {
			entity.pullInputsIntoConstructionStorage(serverLevel);
		} else {
			entity.pushInputsToElevatorCargo(elevator);
		}
	}

	private void pullInputsIntoConstructionStorage(ServerLevel level) {
		SpaceElevatorConstructionRecipe recipe = SpaceElevatorConstructionHandler.getRecipe(level);
		if (recipe == null) {
			return;
		}
		if (SpaceElevatorMaterialStorage.hasAllMaterials(level, worldPosition, recipe)) {
			return;
		}

		boolean changed = false;
		SpaceElevatorMaterialStorage.StoreResult itemResult = SpaceElevatorMaterialStorage.storeFromItemHandler(level, worldPosition, recipe, inputItems);
		if (itemResult != SpaceElevatorMaterialStorage.StoreResult.NOTHING_TO_STORE) {
			changed = true;
		}
		SpaceElevatorMaterialStorage.StoreResult fluidResult = SpaceElevatorMaterialStorage.storeFromFluidHandler(level, worldPosition, recipe, inputFluid);
		if (fluidResult != SpaceElevatorMaterialStorage.StoreResult.NOTHING_TO_STORE) {
			changed = true;
		}
		if (!changed) {
			return;
		}

		setChanged();
		broadcastStoredCounts(level, recipe);
	}

	private void pushInputsToElevatorCargo(SpaceElevatorEntity elevator) {
		boolean[] changed = {false};
		elevator.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(cargo -> {
			if (transferItemsToCargo(cargo)) {
				changed[0] = true;
			}
		});
		elevator.getCapability(ForgeCapabilities.FLUID_HANDLER).ifPresent(cargo -> {
			if (transferFluidToCargo(cargo)) {
				changed[0] = true;
			}
		});
		if (changed[0]) {
			setChanged();
		}
	}

	private boolean transferItemsToCargo(IItemHandler cargo) {
		boolean movedAny = false;
		for (int sourceSlot = 0; sourceSlot < inputItems.getSlots(); sourceSlot++) {
			ItemStack stack = inputItems.getStackInSlot(sourceSlot);
			if (stack.isEmpty()) {
				continue;
			}

			int remaining = stack.getCount();
			for (int targetSlot = 0; targetSlot < cargo.getSlots() && remaining > 0; targetSlot++) {
				ItemStack attempt = stack.copyWithCount(remaining);
				ItemStack leftover = cargo.insertItem(targetSlot, attempt, false);
				int accepted = remaining - leftover.getCount();
				if (accepted > 0) {
					remaining -= accepted;
					movedAny = true;
				}
			}

			int moved = stack.getCount() - remaining;
			if (moved > 0) {
				inputItems.extractItem(sourceSlot, moved, false);
			}
		}
		return movedAny;
	}

	private boolean transferFluidToCargo(IFluidHandler cargoTank) {
		FluidStack stored = inputFluid.getFluid();
		if (stored.isEmpty()) {
			return false;
		}
		int accepted = cargoTank.fill(stored.copy(), IFluidHandler.FluidAction.EXECUTE);
		if (accepted <= 0) {
			return false;
		}
		inputFluid.drain(accepted, IFluidHandler.FluidAction.EXECUTE);
		return true;
	}

	private void broadcastStoredCounts(ServerLevel level, SpaceElevatorConstructionRecipe recipe) {
		int[] counts = SpaceElevatorConstructionHandler.getStoredCounts(level, worldPosition, recipe.ingredients().size());
		int[] fluidAmounts = SpaceElevatorConstructionHandler.getStoredFluidAmounts(level, worldPosition, recipe.fluidIngredients().size());
		boolean orbitalCounterpartPresent = SpaceElevatorConstructionHandler.hasOrbitalCounterpart(level, worldPosition);
		AABB syncBounds = AABB.ofSize(Vec3.atCenterOf(worldPosition), MATERIAL_SYNC_RADIUS * 2.0D, MATERIAL_SYNC_RADIUS * 2.0D, MATERIAL_SYNC_RADIUS * 2.0D);
		for (ServerPlayer player : level.getEntitiesOfClass(ServerPlayer.class, syncBounds)) {
			CmiNetwork.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new SyncSpaceElevatorMaterialsPacket(worldPosition, counts, fluidAmounts, orbitalCounterpartPresent));
		}
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
