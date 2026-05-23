package dev.celestiacraft.cmi.common.block.space_elevator_top;

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
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

public class SpaceElevatorTopBlockEntity extends BlockEntity implements GeoBlockEntity, dev.celestiacraft.cmi.common.entity.space_elevator.ElevatorEnergyAnchor {
	public static final int ENERGY_CAPACITY = 10_000_000;
	public static final int ENERGY_MAX_RECEIVE = 50_000;
	public static final int LAUNCH_ENERGY_COST = 1_000_000;

	public static final String DOOR_CONTROLLER = "door_controller";
	public static final String ANIM_CLOSE_DOOR = "close_door";
	public static final String ANIM_OPEN_DOOR = "open_door";

	private static final RawAnimation IDLE_ANIM = RawAnimation.begin().thenLoop("idle");
	private static final RawAnimation CLOSE_DOOR_ANIM = RawAnimation.begin().thenPlayAndHold(ANIM_CLOSE_DOOR);
	private static final RawAnimation OPEN_DOOR_ANIM = RawAnimation.begin().thenPlayAndHold(ANIM_OPEN_DOOR);

	private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

	private final EnergyStorage energy = new EnergyStorage(ENERGY_CAPACITY, ENERGY_MAX_RECEIVE, 0, 0) {
		@Override
		public int receiveEnergy(int maxReceive, boolean simulate) {
			int received = super.receiveEnergy(maxReceive, simulate);
			if (received > 0 && !simulate) {
				setChanged();
				if (level != null && !level.isClientSide()) {
					level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
				}
			}
			return received;
		}
	};

	@Getter
	private LazyOptional<IEnergyStorage> energyCap = LazyOptional.empty();

	private boolean doorClosed = false;

	public SpaceElevatorTopBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	@Override
	public void onLoad() {
		super.onLoad();
		rebuildCaps();
	}

	private void rebuildCaps() {
		this.energyCap = LazyOptional.of(() -> energy);
	}

	@Override
	public void invalidateCaps() {
		super.invalidateCaps();
		energyCap.invalidate();
	}

	@Override
	public void reviveCaps() {
		super.reviveCaps();
		rebuildCaps();
	}

	@Override
	public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction direction) {
		if (capability == ForgeCapabilities.ENERGY) {
			return energyCap.cast();
		}
		return super.getCapability(capability, direction);
	}

	public int getEnergyStored() {
		return energy.getEnergyStored();
	}

	public int getEnergyCapacity() {
		return ENERGY_CAPACITY;
	}

	public boolean consumeEnergy(int amount) {
		if (energy.getEnergyStored() < amount) {
			return false;
		}
		energy.extractEnergy(amount, false);
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

	public void addEnergyForTesting(int amount) {
		energy.receiveEnergy(amount, false);
	}

	public void playCloseDoor() {
		if (level == null || level.isClientSide()) {
			return;
		}
		doorClosed = true;
		setChanged();
		triggerAnim(DOOR_CONTROLLER, ANIM_CLOSE_DOOR);
	}

	public void playOpenDoor() {
		if (level == null || level.isClientSide()) {
			return;
		}
		doorClosed = false;
		setChanged();
		triggerAnim(DOOR_CONTROLLER, ANIM_OPEN_DOOR);
	}

	@Override
	protected void saveAdditional(@NotNull CompoundTag tag) {
		super.saveAdditional(tag);
		tag.put("Energy", energy.serializeNBT());
		tag.putBoolean("DoorClosed", doorClosed);
	}

	@Override
	public void load(@NotNull CompoundTag tag) {
		super.load(tag);
		if (tag.contains("Energy")) {
			energy.deserializeNBT(tag.get("Energy"));
		}
		doorClosed = tag.getBoolean("DoorClosed");
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
		return new AABB(worldPosition).inflate(6.0D, 12.0D, 6.0D);
	}

	@Override
	public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
		controllers.add(new AnimationController<>(this, "controller", 0, state -> {
			state.getController().setAnimation(IDLE_ANIM);
			return PlayState.CONTINUE;
		}));
		controllers.add(new AnimationController<>(this, DOOR_CONTROLLER, 0, state -> PlayState.STOP)
				.triggerableAnim(ANIM_CLOSE_DOOR, CLOSE_DOOR_ANIM)
				.triggerableAnim(ANIM_OPEN_DOOR, OPEN_DOOR_ANIM));
	}

	@Override
	public AnimatableInstanceCache getAnimatableInstanceCache() {
		return this.cache;
	}
}
