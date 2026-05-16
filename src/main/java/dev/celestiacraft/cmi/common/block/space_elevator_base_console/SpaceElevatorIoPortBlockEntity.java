package dev.celestiacraft.cmi.common.block.space_elevator_base_console;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SpaceElevatorIoPortBlockEntity extends BlockEntity {
	@Nullable
	private BlockPos controllerPos;

	public SpaceElevatorIoPortBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	public void setControllerPos(BlockPos controllerPos) {
		this.controllerPos = controllerPos.immutable();
		setChanged();
		if (level != null && !level.isClientSide()) {
			level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
		}
	}

	@Nullable
	public BlockPos getControllerPos() {
		return controllerPos;
	}

	@Nullable
	private SpaceElevatorBaseConsoleBlockEntity resolveController() {
		if (controllerPos == null || level == null) {
			return null;
		}
		if (level.getBlockEntity(controllerPos) instanceof SpaceElevatorBaseConsoleBlockEntity console) {
			return console;
		}
		return null;
	}

	private IoPortType getIoType() {
		return getBlockState().getValue(SpaceElevatorIoPortBlock.IO_TYPE);
	}

	@Override
	public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction side) {
		SpaceElevatorBaseConsoleBlockEntity console = resolveController();
		if (console == null) {
			return super.getCapability(capability, side);
		}
		IoPortType io = getIoType();
		if (io == IoPortType.NONE) {
			return LazyOptional.empty();
		}
		if (capability == ForgeCapabilities.ENERGY && io == IoPortType.ENERGY_IN) {
			return console.getEnergyCap().cast();
		}
		if (capability == ForgeCapabilities.ITEM_HANDLER) {
			if (io == IoPortType.INPUT_ITEM) {
				return console.getInputItemCap().cast();
			}
			if (io == IoPortType.OUTPUT_ITEM) {
				return console.getOutputItemCap().cast();
			}
		}
		if (capability == ForgeCapabilities.FLUID_HANDLER) {
			if (io == IoPortType.INPUT_FLUID) {
				return console.getInputFluidCap().cast();
			}
			if (io == IoPortType.OUTPUT_FLUID) {
				return console.getOutputFluidCap().cast();
			}
		}
		return LazyOptional.empty();
	}

	@Override
	protected void saveAdditional(@NotNull CompoundTag tag) {
		super.saveAdditional(tag);
		if (controllerPos != null) {
			tag.putLong("ControllerPos", controllerPos.asLong());
		}
	}

	@Override
	public void load(@NotNull CompoundTag tag) {
		super.load(tag);
		if (tag.contains("ControllerPos")) {
			controllerPos = BlockPos.of(tag.getLong("ControllerPos"));
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
}
