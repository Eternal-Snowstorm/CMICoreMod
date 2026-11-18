package dev.celestiacraft.cmi.common.block.space_elevator_top.io;

import org.jetbrains.annotations.NotNull;

import dev.celestiacraft.cmi.common.block.space_elevator_top.SpaceElevatorTopBlockEntity;
import dev.celestiacraft.cmi.common.entity.space_elevator.SpaceElevatorEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.FluidStack;

public class TopInputFluidHandler implements IFluidHandler {
	private static final int TANK_COUNT = 1;

	private final SpaceElevatorTopBlockEntity owner;

	public TopInputFluidHandler(SpaceElevatorTopBlockEntity owner) {
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
		IFluidHandler handler = target();
		if (handler == null || tank < 0 || tank >= handler.getTanks()) {
			return false;
		}
		return handler.isFluidValid(tank, stack);
	}

	@Override
	public int fill(FluidStack resource, FluidAction action) {
		IFluidHandler handler = target();
		if (handler == null || resource.isEmpty()) {
			return 0;
		}
		return handler.fill(resource, action);
	}

	// 输入端口不允许抽取
	@Override
	public @NotNull FluidStack drain(FluidStack resource, FluidAction action) {
		return FluidStack.EMPTY;
	}

	@Override
	public @NotNull FluidStack drain(int maxDrain, FluidAction action) {
		return FluidStack.EMPTY;
	}
}
