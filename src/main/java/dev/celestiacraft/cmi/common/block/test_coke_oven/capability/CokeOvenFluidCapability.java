package dev.celestiacraft.cmi.common.block.test_coke_oven.capability;

import dev.celestiacraft.cmi.common.block.test_coke_oven.TestCokeOvenBlockEntity;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;

public class CokeOvenFluidCapability implements IFluidHandler {
	private final TestCokeOvenBlockEntity entity;

	public CokeOvenFluidCapability(TestCokeOvenBlockEntity entity) {
		this.entity = entity;
	}

	@Override
	public int getTanks() {
		return 1;
	}

	@Override
	public @NotNull FluidStack getFluidInTank(int tank) {
		return entity.isStructureValid() ? entity.getFluid().copy() : FluidStack.EMPTY;
	}

	@Override
	public int getTankCapacity(int tank) {
		return 4000;
	}

	@Override
	public boolean isFluidValid(int tank, @NotNull FluidStack stack) {
		FluidStack fluid = entity.getFluid();
		return entity.isStructureValid() &&
				(fluid.isEmpty() || stack.isFluidEqual(fluid));
	}

	@Override
	public int fill(FluidStack stack, FluidAction action) {
		return entity.fillFluid(stack, action);
	}

	@Override
	public @NotNull FluidStack drain(FluidStack stack, FluidAction action) {
		return entity.drainFluid(stack.getAmount(), action);
	}

	@Override
	public @NotNull FluidStack drain(int amount, FluidAction action) {
		return entity.drainFluid(amount, action);
	}
}