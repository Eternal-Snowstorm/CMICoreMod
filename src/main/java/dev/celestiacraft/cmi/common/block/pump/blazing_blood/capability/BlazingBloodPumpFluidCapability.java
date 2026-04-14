package dev.celestiacraft.cmi.common.block.pump.blazing_blood.capability;

import dev.celestiacraft.cmi.common.block.pump.blazing_blood.BlazingBloodPumpBlockEntity;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.fluids.TinkerFluids;

public class BlazingBloodPumpFluidCapability implements IFluidHandler {
	private final BlazingBloodPumpBlockEntity entity;

	public BlazingBloodPumpFluidCapability(BlazingBloodPumpBlockEntity entity) {
		this.entity = entity;
	}

	@Override
	public int getTanks() {
		return 1;
	}

	@Override
	public @NotNull FluidStack getFluidInTank(int amount) {
		if (entity.isStructureValid() && entity.isWorkEnv()) {
			return new FluidStack(TinkerFluids.blazingBlood.get(), Integer.MAX_VALUE);
		}
		return FluidStack.EMPTY;
	}

	@Override
	public int getTankCapacity(int amount) {
		return Integer.MAX_VALUE;
	}

	@Override
	public boolean isFluidValid(int amount, @NotNull FluidStack fluidStack) {
		return false;
	}

	@Override
	public int fill(FluidStack fluidStack, FluidAction fluidAction) {
		return 0;
	}

	@Override
	public @NotNull FluidStack drain(FluidStack fluidStack, FluidAction fluidAction) {
		if (entity.isStructureValid() && entity.isWorkEnv()) {
			if (fluidStack.getFluid() == TinkerFluids.blazingBlood.get()) {
				return fluidStack;
			}
			return FluidStack.EMPTY;
		}
		return FluidStack.EMPTY;
	}

	@Override
	public @NotNull FluidStack drain(int amount, FluidAction fluidAction) {
		if (entity.isStructureValid() && entity.isWorkEnv()) {
			return new FluidStack(TinkerFluids.blazingBlood.get(), amount);
		}
		return FluidStack.EMPTY;
	}
}