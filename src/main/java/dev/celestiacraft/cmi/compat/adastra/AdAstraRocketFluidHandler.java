package dev.celestiacraft.cmi.compat.adastra;

import org.jetbrains.annotations.NotNull;

import earth.terrarium.adastra.common.entities.vehicles.Rocket;
import earth.terrarium.botarium.common.fluid.base.FluidContainer;
import earth.terrarium.botarium.common.fluid.base.FluidHolder;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.FluidStack;

public class AdAstraRocketFluidHandler implements IFluidHandler {
	private final Rocket rocket;

	public AdAstraRocketFluidHandler(@NotNull Rocket rocket) {
		this.rocket = rocket;
	}

	// 取火箭的 botarium 流体容器
	private FluidContainer container() {
		return rocket.fluidContainer();
	}

	@Override
	public int getTanks() {
		return container().getSize();
	}

	@Override
	public @NotNull FluidStack getFluidInTank(int tank) {
		FluidContainer container = container();
		if (tank < 0 || tank >= container.getSize()) {
			return FluidStack.EMPTY;
		}
		return toStack(container.getFluids().get(tank));
	}

	@Override
	public int getTankCapacity(int tank) {
		FluidContainer container = container();
		if (tank < 0 || tank >= container.getSize()) {
			return 0;
		}
		return toInt(container.getTankCapacity(tank));
	}

	@Override
	public boolean isFluidValid(int tank, @NotNull FluidStack stack) {
		FluidContainer container = container();
		if (stack.isEmpty() || tank < 0 || tank >= container.getSize()) {
			return false;
		}
		return container.isFluidValid(tank, FluidHolder.ofMillibuckets(stack.getFluid(), stack.getAmount()));
	}

	@Override
	public int fill(@NotNull FluidStack resource, @NotNull FluidAction action) {
		FluidContainer container = container();
		if (resource.isEmpty() || !container.allowsInsertion()) {
			return 0;
		}
		long inserted = container.insertFluid(
				FluidHolder.ofMillibuckets(resource.getFluid(), resource.getAmount()),
				action.simulate()
		);
		return toInt(inserted);
	}

	@Override
	public @NotNull FluidStack drain(@NotNull FluidStack resource, @NotNull FluidAction action) {
		FluidContainer container = container();
		if (resource.isEmpty() || !container.allowsExtraction()) {
			return FluidStack.EMPTY;
		}
		FluidHolder extracted = container.extractFluid(
				FluidHolder.ofMillibuckets(resource.getFluid(), resource.getAmount()),
				action.simulate()
		);
		return toStack(extracted);
	}

	@Override
	public @NotNull FluidStack drain(int maxDrain, @NotNull FluidAction action) {
		FluidContainer container = container();
		if (maxDrain <= 0 || !container.allowsExtraction()) {
			return FluidStack.EMPTY;
		}
		FluidHolder first = container.getFirstFluid();
		if (first.isEmpty()) {
			return FluidStack.EMPTY;
		}
		FluidHolder extracted = container.extractFluid(
				FluidHolder.ofMillibuckets(first.getFluid(), maxDrain),
				action.simulate()
		);
		return toStack(extracted);
	}

	private static FluidStack toStack(@NotNull FluidHolder holder) {
		if (holder.isEmpty()) {
			return FluidStack.EMPTY;
		}
		return new FluidStack(holder.getFluid(), toInt(holder.getFluidAmount()));
	}

	private static int toInt(long amount) {
		return (int) Math.min(Integer.MAX_VALUE, amount);
	}
}