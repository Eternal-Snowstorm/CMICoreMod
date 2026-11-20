package dev.celestiacraft.cmi.common.block.space_elevator_top.io;

import dev.celestiacraft.cmi.common.block.space_elevator_top.SpaceElevatorTopBlockEntity;
import dev.celestiacraft.cmi.common.entity.space_elevator.SpaceElevatorEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;

public class TopOutputFluidHandler implements IFluidHandler {
	private static final int TANK_COUNT = 1;

	private final SpaceElevatorTopBlockEntity owner;

	public TopOutputFluidHandler(SpaceElevatorTopBlockEntity owner) {
		this.owner = owner;
	}

	// 取锚定电梯货舱的流体能力，没有电梯时返回空
	private IFluidHandler target() {
		SpaceElevatorEntity elevator = owner.findElevator();
		if (elevator == null) {
			return null;
		}
		return elevator.getCapability(ForgeCapabilities.FLUID_HANDLER).orElse(null);
	}

	@Override
	public int getTanks() {
		return TANK_COUNT;
	}

	@Override
	public @NotNull FluidStack getFluidInTank(int tank) {
		IFluidHandler handler = target();
		if (handler == null || tank < 0 || tank >= handler.getTanks()) {
			return FluidStack.EMPTY;
		}
		return handler.getFluidInTank(tank).copy();
	}

	@Override
	public int getTankCapacity(int tank) {
		IFluidHandler handler = target();
		if (handler == null || tank < 0 || tank >= handler.getTanks()) {
			return SpaceElevatorEntity.CARGO_FLUID_CAPACITY;
		}
		return handler.getTankCapacity(tank);
	}

	@Override
	public boolean isFluidValid(int tank, @NotNull FluidStack stack) {
		return false;
	}

	// 输出端口不允许注入
	@Override
	public int fill(FluidStack resource, FluidAction action) {
		return 0;
	}

	@Override
	public @NotNull FluidStack drain(FluidStack resource, FluidAction action) {
		IFluidHandler handler = target();
		if (handler == null || resource.isEmpty()) {
			return FluidStack.EMPTY;
		}
		return handler.drain(resource, action);
	}

	@Override
	public @NotNull FluidStack drain(int maxDrain, FluidAction action) {
		IFluidHandler handler = target();
		if (handler == null || maxDrain <= 0) {
			return FluidStack.EMPTY;
		}
		return handler.drain(maxDrain, action);
	}
}