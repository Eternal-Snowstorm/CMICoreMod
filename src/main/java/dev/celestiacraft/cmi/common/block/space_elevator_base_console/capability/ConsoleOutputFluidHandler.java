package dev.celestiacraft.cmi.common.block.space_elevator_base_console.capability;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.NotNull;

public class ConsoleOutputFluidHandler implements IFluidHandler {
	private final FluidTank backing;

	public ConsoleOutputFluidHandler(FluidTank backing) {
		this.backing = backing;
	}

	@Override
	public int getTanks() {
		return 1;
	}

	@Override
	public @NotNull FluidStack getFluidInTank(int tank) {
		return backing.getFluid();
	}

	@Override
	public int getTankCapacity(int tank) {
		return backing.getCapacity();
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
		return backing.drain(resource, action);
	}

	@Override
	public @NotNull FluidStack drain(int maxDrain, FluidAction action) {
		return backing.drain(maxDrain, action);
	}
}
