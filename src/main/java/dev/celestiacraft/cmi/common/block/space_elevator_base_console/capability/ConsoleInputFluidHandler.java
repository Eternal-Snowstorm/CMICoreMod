package dev.celestiacraft.cmi.common.block.space_elevator_base_console.capability;

import org.jetbrains.annotations.NotNull;

import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.fluids.FluidStack;

public class ConsoleInputFluidHandler implements IFluidHandler {
	private final FluidTank[] tanks;

	public ConsoleInputFluidHandler(FluidTank[] tanks) {
		this.tanks = tanks;
	}

	@Override
	public int getTanks() {
		return tanks.length;
	}

	@Override
	public @NotNull FluidStack getFluidInTank(int tank) {
		if (tank < 0 || tank >= tanks.length) {
			return FluidStack.EMPTY;
		}
		return tanks[tank].getFluid().copy();
	}

	@Override
	public int getTankCapacity(int tank) {
		if (tank < 0 || tank >= tanks.length) {
			return 0;
		}
		return tanks[tank].getCapacity();
	}

	@Override
	public boolean isFluidValid(int tank, @NotNull FluidStack stack) {
		return !stack.getFluid().isSame(Fluids.EMPTY);
	}

	@Override
	public int fill(FluidStack resource, FluidAction action) {
		if (resource.isEmpty()) {
			return 0;
		}
		FluidStack remaining = fillIntoTanks(resource, action, false);
		remaining = fillIntoTanks(remaining, action, true);
		return resource.getAmount() - remaining.getAmount();
	}

	// 按同种液体罐优先、空罐其次的顺序，跨罐累计注入流体
	private FluidStack fillIntoTanks(FluidStack resource, FluidAction action, boolean emptyTanks) {
		FluidStack remaining = resource.copy();
		for (FluidTank tank : tanks) {
			if (remaining.isEmpty()) {
				break;
			}
			FluidStack existing = tank.getFluid();
			if (existing.isEmpty() != emptyTanks) {
				continue;
			}
			if (!emptyTanks && !existing.isFluidEqual(remaining)) {
				continue;
			}
			remaining.shrink(tank.fill(remaining, action));
		}
		return remaining;
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
