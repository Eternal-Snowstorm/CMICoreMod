package dev.celestiacraft.cmi.common.block.well.lava.capability;

import dev.celestiacraft.cmi.common.block.well.lava.LavaWellBlockEntity;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;

public class LavaWellFluidCapability implements IFluidHandler {
	private final LavaWellBlockEntity entity;

	public LavaWellFluidCapability(LavaWellBlockEntity entity) {
		this.entity = entity;
	}

	@Override
	public int getTanks() {
		return 1;
	}

	@Override
	public @NotNull FluidStack getFluidInTank(int amount) {
		if (entity.isStructureValid() && entity.isWorkConditions()) {
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
		if (entity.isStructureValid() && entity.isWorkConditions()) {
			if (fluidStack.getFluid() == Fluids.LAVA) {
				return fluidStack;
			}
			return FluidStack.EMPTY;
		}
		return FluidStack.EMPTY;
	}

	@Override
	public @NotNull FluidStack drain(int amount, FluidAction fluidAction) {
		if (entity.isStructureValid() && entity.isWorkConditions()) {
			return new FluidStack(Fluids.LAVA, amount);
		}
		return FluidStack.EMPTY;
	}
}