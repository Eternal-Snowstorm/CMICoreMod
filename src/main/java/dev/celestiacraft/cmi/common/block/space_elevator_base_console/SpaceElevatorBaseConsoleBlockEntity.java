package dev.celestiacraft.cmi.common.block.space_elevator_base_console;

import dev.celestiacraft.cmi.common.block.space_elevator_base_console.capability.*;
import dev.celestiacraft.cmi.common.block.space_elevator_base_console.transfer.SpaceElevatorConsoleTransferEvent;
import dev.celestiacraft.cmi.common.entity.space_elevator.ElevatorEnergyAnchor;
import dev.celestiacraft.cmi.common.entity.space_elevator.SpaceElevatorConsoleDisplayState;
import dev.celestiacraft.cmi.common.entity.space_elevator.SpaceElevatorEntity;
import dev.celestiacraft.cmi.common.recipe.space_elevator_construction.SpaceElevatorConstructionRecipe;
import dev.celestiacraft.cmi.compat.adastra.SpaceElevatorConstructionHandler;
import dev.celestiacraft.cmi.compat.adastra.SpaceElevatorLinkHandler;
import dev.celestiacraft.cmi.network.CmiNetwork;
import dev.celestiacraft.cmi.network.s2c.SyncSpaceElevatorMaterialsPacket;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
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
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
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

public class SpaceElevatorBaseConsoleBlockEntity extends BlockEntity implements GeoBlockEntity, ElevatorEnergyAnchor {
	public static final int ENERGY_CAPACITY = 10_000_000;
	public static final int ENERGY_MAX_RECEIVE = 50_000;
	public static final int LAUNCH_ENERGY_COST = 1_000_000;
	public static final int FLUID_TANK_CAPACITY = 64_000;
	public static final int FLUID_TANK_COUNT = 4;
	public static final int ITEM_SLOT_COUNT = 128;

	public static final Direction INPUT_SIDE = Direction.EAST;
	public static final Direction OUTPUT_SIDE = Direction.WEST;
	public static final Direction ENERGY_SIDE = Direction.SOUTH;

	private static final int INPUT_PULL_INTERVAL_TICKS = 20;
	private static final int ELEVATOR_CHECK_INTERVAL_TICKS = 10;
	private static final double MATERIAL_SYNC_RADIUS = 32.0D;

	private static final RawAnimation IDLE_ANIM = RawAnimation.begin().thenLoop("idle");

	private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

	@Getter
	private int energyStored = 0;

	@Getter
	private boolean elevatorPresent = false;
	@Getter
	private SpaceElevatorConsoleDisplayState elevatorDisplayState = SpaceElevatorConsoleDisplayState.READY;

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
	private final FluidTank[] inputFluids = createTankArray();
	private final FluidTank[] outputFluids = createTankArray();

	public FluidTank[] getInputFluids() {
		return inputFluids;
	}

	public FluidTank[] getOutputFluids() {
		return outputFluids;
	}

	private FluidTank[] createTankArray() {
		FluidTank[] array = new FluidTank[FLUID_TANK_COUNT];
		for (int i = 0; i < FLUID_TANK_COUNT; i++) {
			array[i] = new FluidTank(FLUID_TANK_CAPACITY) {
				@Override
				protected void onContentsChanged() {
					setChanged();
				}
			};
		}
		return array;
	}

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
		this.inputFluidCap = LazyOptional.of(() -> new ConsoleInputFluidHandler(inputFluids));
		this.outputFluidCap = LazyOptional.of(() -> new ConsoleOutputFluidHandler(outputFluids));
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
		SpaceElevatorEntity elevator = null;
		if ((serverLevel.getGameTime() + pos.asLong()) % ELEVATOR_CHECK_INTERVAL_TICKS == 0) {
			elevator = SpaceElevatorConstructionHandler.getNearbyElevator(serverLevel, pos);
			boolean nowPresent = elevator != null || SpaceElevatorConstructionHandler.hasOrbitalCounterpart(serverLevel, pos);
			SpaceElevatorConsoleDisplayState displayState = elevator != null
					? elevator.getConsoleDisplayState()
					: SpaceElevatorLinkHandler.getElevatorDisplayState(serverLevel, pos);
			if (nowPresent != entity.elevatorPresent || displayState != entity.elevatorDisplayState) {
				entity.elevatorPresent = nowPresent;
				entity.elevatorDisplayState = displayState;
				entity.setChanged();
				serverLevel.sendBlockUpdated(pos, state, state, 3);
			}
		}
		if ((serverLevel.getGameTime() + pos.asLong()) % INPUT_PULL_INTERVAL_TICKS != 0) {
			return;
		}
		if (elevator == null) {
			elevator = SpaceElevatorConstructionHandler.getNearbyElevator(serverLevel, pos);
		}
		if (elevator == null) {
			entity.broadcastConstructionMaterials(serverLevel);
		} else if (!elevator.isAwaitingUnload()) {
			entity.pushInputsToElevatorCargo(serverLevel, elevator);
		} else if (elevator.isAutoUnload()) {
			entity.unloadElevatorCargo(serverLevel, elevator);
		}
	}

	private void broadcastConstructionMaterials(ServerLevel level) {
		SpaceElevatorConstructionRecipe recipe = SpaceElevatorConstructionHandler.getRecipe(level);
		if (recipe == null) {
			return;
		}
		broadcastStoredCounts(level, recipe);
	}

	private void pushInputsToElevatorCargo(ServerLevel level, SpaceElevatorEntity elevator) {
		boolean itemsMoved = elevator.getCapability(ForgeCapabilities.ITEM_HANDLER)
				.map(cargo -> transferItems(inputItems, cargo))
				.orElse(false);
		boolean fluidMoved = elevator.getCapability(ForgeCapabilities.FLUID_HANDLER)
				.map(cargo -> transferFluidToCargo(cargo) > 0)
				.orElse(false);
		if (itemsMoved || fluidMoved) {
			onCargoTransferred(level);
		}
	}

	public void unloadElevatorCargo(ServerLevel level, SpaceElevatorEntity elevator) {
		if (elevator.isCurrentlyTransporting()) {
			return;
		}
		boolean itemsMoved = elevator.getCapability(ForgeCapabilities.ITEM_HANDLER)
				.map(cargo -> transferItems(cargo, outputItems))
				.orElse(false);
		boolean fluidMoved = elevator.getCapability(ForgeCapabilities.FLUID_HANDLER)
				.map(cargo -> transferFluidToOutputs(cargo) > 0)
				.orElse(false);
		if (itemsMoved || fluidMoved) {
			onCargoTransferred(level);
		}
	}

	private void onCargoTransferred(ServerLevel level) {
		setChanged();
		MinecraftForge.EVENT_BUS.post(new SpaceElevatorConsoleTransferEvent(level, worldPosition));
	}

	private static boolean transferItems(IItemHandler source, IItemHandler target) {
		boolean movedAny = false;
		for (int slot = 0; slot < source.getSlots(); slot++) {
			ItemStack stack = source.getStackInSlot(slot);
			if (stack.isEmpty()) {
				continue;
			}
			ItemStack leftover = ItemHandlerHelper.insertItemStacked(target, stack.copy(), false);
			int moved = stack.getCount() - leftover.getCount();
			if (moved > 0) {
				source.extractItem(slot, moved, false);
				movedAny = true;
			}
		}
		return movedAny;
	}

	private int transferFluidToCargo(IFluidHandler cargoTank) {
		int moved = 0;
		for (FluidTank tank : inputFluids) {
			FluidStack stored = tank.getFluid();
			if (stored.isEmpty()) {
				continue;
			}
			int accepted = cargoTank.fill(stored.copy(), IFluidHandler.FluidAction.EXECUTE);
			if (accepted <= 0) {
				continue;
			}
			tank.drain(accepted, IFluidHandler.FluidAction.EXECUTE);
			moved += accepted;
		}
		return moved;
	}

	private int transferFluidToOutputs(IFluidHandler cargoTank) {
		FluidStack available = cargoTank.drain(Integer.MAX_VALUE, IFluidHandler.FluidAction.SIMULATE);
		if (available.isEmpty()) {
			return 0;
		}
		FluidStack remaining = available.copy();
		fillOutputTanks(remaining, false);
		fillOutputTanks(remaining, true);
		int accepted = available.getAmount() - remaining.getAmount();
		if (accepted > 0) {
			cargoTank.drain(accepted, IFluidHandler.FluidAction.EXECUTE);
		}
		return accepted;
	}

	private void fillOutputTanks(FluidStack remaining, boolean emptyTanks) {
		for (FluidTank tank : outputFluids) {
			if (remaining.isEmpty()) {
				return;
			}
			if (tank.isEmpty() == emptyTanks) {
				remaining.shrink(tank.fill(remaining, IFluidHandler.FluidAction.EXECUTE));
			}
		}
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
		tag.putBoolean("ElevatorPresent", elevatorPresent);
		tag.putString("ElevatorDisplayState", elevatorDisplayState.getSerializedName());
		tag.put("InputItems", inputItems.serializeNBT());
		tag.put("OutputItems", outputItems.serializeNBT());
		tag.put("InputFluids", serializeTanks(inputFluids));
		tag.put("OutputFluids", serializeTanks(outputFluids));
	}

	@Override
	public void load(@NotNull CompoundTag tag) {
		super.load(tag);
		energyStored = tag.getInt("Energy");
		elevatorPresent = tag.getBoolean("ElevatorPresent");
		elevatorDisplayState = SpaceElevatorConsoleDisplayState.fromSerializedName(tag.getString("ElevatorDisplayState"));
		inputItems.deserializeNBT(tag.getCompound("InputItems"));
		outputItems.deserializeNBT(tag.getCompound("OutputItems"));
		deserializeTanks(inputFluids, tag, "InputFluids", "InputFluid");
		deserializeTanks(outputFluids, tag, "OutputFluids", "OutputFluid");
	}

	private static ListTag serializeTanks(FluidTank[] tanks) {
		ListTag list = new ListTag();
		for (FluidTank tank : tanks) {
			list.add(tank.writeToNBT(new CompoundTag()));
		}
		return list;
	}

	private static void deserializeTanks(FluidTank[] tanks, CompoundTag tag, String listKey, String legacySingleKey) {
		for (FluidTank tank : tanks) {
			tank.setFluid(FluidStack.EMPTY);
		}
		if (tag.contains(listKey, Tag.TAG_LIST)) {
			ListTag list = tag.getList(listKey, Tag.TAG_COMPOUND);
			int count = Math.min(list.size(), tanks.length);
			for (int i = 0; i < count; i++) {
				tanks[i].readFromNBT(list.getCompound(i));
			}
			return;
		}
		if (tag.contains(legacySingleKey, Tag.TAG_COMPOUND)) {
			tanks[0].readFromNBT(tag.getCompound(legacySingleKey));
		}
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
		controllers.add(new AnimationController<>(this, "controller", 0, (state) -> {
			state.getController().setAnimation(IDLE_ANIM);
			return PlayState.CONTINUE;
		}).triggerableAnim("idle", IDLE_ANIM));
	}

	@Override
	public AnimatableInstanceCache getAnimatableInstanceCache() {
		return this.cache;
	}
}
