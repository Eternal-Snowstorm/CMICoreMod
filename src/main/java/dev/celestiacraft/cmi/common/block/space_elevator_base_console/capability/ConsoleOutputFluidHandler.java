package dev.celestiacraft.cmi.common.block.space_elevator_base_console.capability;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.NotNull;

public class ConsoleOutputFluidHandler implements IFluidHandler {
	private final FluidTank[] tanks;

	public ConsoleOutputFluidHandler(FluidTank[] tanks) {
		this.tanks = tanks;
	}

	@Override
	public int getTanks() {
		return tanks.length;
	}

	@Override
	public @NotNull FluidStack getFluidInTank(int tank) {
		if (tank < 0 || tank >= tanks.length) {
			return FluidStack.EMPTY;
		}
		return tanks[tank].getFluid().copy();
	}

	@Override
	public int getTankCapacity(int tank) {
		if (tank < 0 || tank >= tanks.length) {
			return 0;
		}
		return tanks[tank].getCapacity();
	}

	@Override
	public boolean isFluidValid(int tank, @NotNull FluidStack stack) {
		return false;
	}

	@Override
	public int fill(FluidStack resource, FluidAction action) {
		return 0;
	}

	@Override
	public @NotNull FluidStack drain(FluidStack resource, FluidAction action) {
		if (resource.isEmpty()) {
			return FluidStack.EMPTY;
		}
		FluidStack result = FluidStack.EMPTY;
		for (FluidTank tank : tanks) {
			if (result.getAmount() >= resource.getAmount()) {
				break;
			}
			FluidStack existing = tank.getFluid();
			if (existing.isEmpty() || !existing.isFluidEqual(resource)) {
				continue;
			}
			int request = resource.getAmount() - result.getAmount();
			FluidStack drained = tank.drain(request, action);
			if (drained.isEmpty()) {
				continue;
			}
			if (result.isEmpty()) {
				result = drained.copy();
			} else {
				result.grow(drained.getAmount());
			}
		}
		return result;
	}

	@Override
	public @NotNull FluidStack drain(int maxDrain, FluidAction action) {
		if (maxDrain <= 0) {
			return FluidStack.EMPTY;
		}
		for (FluidTank tank : tanks) {
			if (!tank.getFluid().isEmpty()) {
				FluidStack drained = tank.drain(maxDrain, action);
				if (!drained.isEmpty()) {
					return drained;
				}
			}
		}
		return FluidStack.EMPTY;
	}
}