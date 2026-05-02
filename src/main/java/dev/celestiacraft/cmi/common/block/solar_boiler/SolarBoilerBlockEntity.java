package dev.celestiacraft.cmi.common.block.solar_boiler;

import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import dev.celestiacraft.cmi.common.block.solar_boiler.capability.SolarBoilerFluidCapability;
import dev.celestiacraft.cmi.common.block.solar_boiler.capability.SolarBoilerFluidTank;
import dev.celestiacraft.cmi.utils.ModResources;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public abstract class SolarBoilerBlockEntity extends SmartBlockEntity {
	protected final SolarBoilerFluidTank waterTank;
	protected final SolarBoilerFluidTank steamTank;

	private SolarBoilerFluidCapability fluidCapability;

	public SolarBoilerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		int capacity = getFluidCapacity();
		waterTank = new SolarBoilerFluidTank(capacity, (stack) -> {
			return stack.getFluid().is(FluidTags.WATER);
		}, this::setChanged);

		steamTank = new SolarBoilerFluidTank(
				capacity,
				(stack) -> true,
				this::setChanged
		);

		fluidCapability = new SolarBoilerFluidCapability(waterTank, steamTank);
	}

	/**
	 * 每 Tick 消耗的水
	 *
	 * @return
	 */
	protected abstract int getWaterConsumptionPerTick();

	/**
	 * 容量
	 *
	 * @return
	 */
	protected abstract int getFluidCapacity();

	@Override
	public void tick() {
		super.tick();

		if (level == null || level.isClientSide()) {
			return;
		}

		if (!canWork()) {
			return;
		}

		process();
	}

	protected boolean canWork() {
		return level.isDay()
				&& level.canSeeSky(worldPosition.above())
				&& !level.isRaining();
	}

	protected void process() {
		int consume = getWaterConsumptionPerTick();
		if (consume <= 0) {
			return;
		}

		// 水检查
		FluidStack water = waterTank.getFluid();
		if (water.isEmpty() || !water.getFluid().is(FluidTags.WATER)) {
			return;
		}
		if (waterTank.getFluidAmount() < consume) {
			return;
		}

		// 空间检查
		if (steamTank.getSpace() < consume) {
			return;
		}

		FluidStack steam = ModResources.STEAM.getFluidStack(consume);

		int filled = steamTank.fill(steam, IFluidHandler.FluidAction.SIMULATE);
		if (filled <= 0) {
			return;
		}

		// 执行
		waterTank.drain(consume, IFluidHandler.FluidAction.EXECUTE);
		steamTank.fill(steam, IFluidHandler.FluidAction.EXECUTE);

		setChanged();
	}

	@Override
	public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
	}

	@Override
	public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> capability, Direction direction) {
		if (capability == ForgeCapabilities.FLUID_HANDLER) {
			return fluidCapability.get(direction).cast();
		}
		return super.getCapability(capability, direction);
	}

	@Override
	public void invalidateCaps() {
		super.invalidateCaps();
		fluidCapability.invalidate();
	}

	@Override
	public void reviveCaps() {
		super.reviveCaps();
		fluidCapability = new SolarBoilerFluidCapability(waterTank, steamTank);
	}

	@Override
	protected void write(CompoundTag tag, boolean clientPacket) {
		super.write(tag, clientPacket);
		tag.put("WaterTank", waterTank.writeToNBT(new CompoundTag()));
		tag.put("SteamTank", steamTank.writeToNBT(new CompoundTag()));
	}

	@Override
	protected void read(CompoundTag tag, boolean clientPacket) {
		super.read(tag, clientPacket);
		waterTank.readFromNBT(tag.getCompound("WaterTank"));
		steamTank.readFromNBT(tag.getCompound("SteamTank"));
	}

	@Override
	public @NotNull CompoundTag getUpdateTag() {
		return saveWithoutMetadata();
	}

	@Override
	public void handleUpdateTag(@NotNull CompoundTag tag) {
		load(tag);
	}
}