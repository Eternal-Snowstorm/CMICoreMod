package dev.celestiacraft.cmi.common.block.solar_boiler.capability;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

public class SolarBoilerFluidTank extends FluidTank {
	private final Runnable onChange;

	public SolarBoilerFluidTank(int capacity, Predicate<FluidStack> validator, Runnable onChange) {
		super(capacity, validator);
		this.onChange = onChange;
	}

	@Override
	protected void onContentsChanged() {
		onChange.run();
	}

	public boolean canFill() {
		return true;
	}

	public boolean canDrain() {
		return true;
	}

	@Override
	public int fill(FluidStack resource, FluidAction action) {
		if (!canFill()) {
			return 0;
		}
		return super.fill(resource, action);
	}

	@Override
	public @NotNull FluidStack drain(int maxDrain, FluidAction action) {
		if (!canDrain()) {
			return FluidStack.EMPTY;
		}
		return super.drain(maxDrain, action);
	}
}