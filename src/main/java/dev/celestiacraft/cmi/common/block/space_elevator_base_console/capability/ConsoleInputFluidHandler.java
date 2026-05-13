package dev.celestiacraft.cmi.common.block.space_elevator_base_console.capability;

import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.NotNull;

public class ConsoleInputFluidHandler implements IFluidHandler {
	private final FluidTank backing;

	public ConsoleInputFluidHandler(FluidTank backing) {
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
		return !stack.getFluid().isSame(Fluids.EMPTY);
	}

	@Override
	public int fill(FluidStack resource, FluidAction action) {
		return backing.fill(resource, action);
	}

	@Override
	public @NotNull FluidStack drain(FluidStack resource, FluidAction action) {
		return FluidStack.EMPTY;
	}

	@Override
	public @NotNull FluidStack drain(int maxDrain, FluidAction action) {
		return FluidStack.EMPTY;
	}
}
