package dev.celestiacraft.cmi.common.block.test_coke_oven.capability;

import dev.celestiacraft.cmi.common.block.test_coke_oven.TestCokeOvenBlockEntity;
import dev.celestiacraft.cmi.common.block.test_coke_oven.TestCokeOvenIOBlockEntity;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;

public class CokeOvenFluidCapability implements IFluidHandler {
	private final TestCokeOvenIOBlockEntity entity;

	public CokeOvenFluidCapability(TestCokeOvenIOBlockEntity entity) {
		this.entity = entity;
	}

	@Override
	public int getTanks() {
		return 1;
	}

	@Override
	public @NotNull FluidStack getFluidInTank(int tank) {
		return entity.getFluid().copy();
	}

	@Override
	public int getTankCapacity(int tank) {
		return 4000;
	}

	@Override
	public boolean isFluidValid(int tank, @NotNull FluidStack stack) {
		FluidStack fluid = entity.getFluid();
		return (fluid.isEmpty() || stack.isFluidEqual(fluid));
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