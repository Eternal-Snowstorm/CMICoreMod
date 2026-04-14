package dev.celestiacraft.cmi.common.block.pump.lava.capability;

import dev.celestiacraft.cmi.common.block.pump.lava.LavaPumpBlockEntity;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;

public class LavaPumpFluidCapability implements IFluidHandler {
	private final LavaPumpBlockEntity entity;

	public LavaPumpFluidCapability(LavaPumpBlockEntity entity) {
		this.entity = entity;
	}

	@Override
	public int getTanks() {
		return 1;
	}

	@Override
	public @NotNull FluidStack getFluidInTank(int amount) {
		if (entity.isStructureValid() && entity.isWorkEnv()) {
			return new FluidStack(Fluids.LAVA, Integer.MAX_VALUE);
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
			if (fluidStack.getFluid() == Fluids.LAVA) {
				return fluidStack;
			}
			return FluidStack.EMPTY;
		}
		return FluidStack.EMPTY;
	}

	@Override
	public @NotNull FluidStack drain(int amount, FluidAction fluidAction) {
		if (entity.isStructureValid() && entity.isWorkEnv()) {
			return new FluidStack(Fluids.LAVA, amount);
		}
		return FluidStack.EMPTY;
	}
}