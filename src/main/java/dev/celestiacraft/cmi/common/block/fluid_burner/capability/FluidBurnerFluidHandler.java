package dev.celestiacraft.cmi.common.block.fluid_burner.capability;

import dev.celestiacraft.cmi.common.block.fluid_burner.FluidBurnerBlockEntity;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;

public class FluidBurnerFluidHandler implements IFluidHandler {
	private final FluidBurnerBlockEntity entity;

	public FluidBurnerFluidHandler(FluidBurnerBlockEntity entity) {
		this.entity = entity;
	}

	@Override
	public int getTanks() {
		return 1;
	}

	@Override
	public @NotNull FluidStack getFluidInTank(int tank) {
		if (tank != 0) {
			return FluidStack.EMPTY;
		}
		return entity.getFluid();
	}

	@Override
	public int getTankCapacity(int tank) {
		if (tank != 0) {
			return 0;
		}
		return entity.getFluidTankCapacity();
	}

	@Override
	public boolean isFluidValid(int tank, @NotNull FluidStack stack) {
		return tank == 0 && entity.canFillFluid(stack);
	}

	@Override
	public int fill(FluidStack resource, FluidAction action) {
		return entity.fillFluid(resource, action);
	}

	@Override
	public @NotNull FluidStack drain(int maxDrain, FluidAction action) {
		return entity.drainFluid(maxDrain, action);
	}

	@Override
	public @NotNull FluidStack drain(FluidStack resource, FluidAction action) {
		return entity.drainFluid(resource, action);
	}
}