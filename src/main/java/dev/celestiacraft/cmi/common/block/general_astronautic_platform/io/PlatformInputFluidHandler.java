package dev.celestiacraft.cmi.common.block.general_astronautic_platform.io;

import dev.celestiacraft.cmi.common.block.general_astronautic_platform.GeneralAstronauticPlatformBlockEntity;
import dev.celestiacraft.cmi.common.entity.prospecting_rocket.ProspectingRocketTier;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;

public class PlatformInputFluidHandler implements IFluidHandler {
	private static final int TANK_COUNT = 1;
	private static final int MAX_CAPACITY = ProspectingRocketTier.TIER_4.fuelCapacity();

	private final GeneralAstronauticPlatformBlockEntity owner;

	public PlatformInputFluidHandler(GeneralAstronauticPlatformBlockEntity owner) {
		this.owner = owner;
	}

	@Override
	public int getTanks() {
		return TANK_COUNT;
	}

	@Override
	public @NotNull FluidStack getFluidInTank(int tank) {
		IFluidHandler handler = owner.getRocketFluid();
		if (handler == null || tank < 0 || tank >= handler.getTanks()) {
			return FluidStack.EMPTY;
		}
		return handler.getFluidInTank(tank).copy();
	}

	@Override
	public int getTankCapacity(int tank) {
		IFluidHandler handler = owner.getRocketFluid();
		if (handler == null || tank < 0 || tank >= handler.getTanks()) {
			return MAX_CAPACITY;
		}
		return handler.getTankCapacity(tank);
	}

	@Override
	public boolean isFluidValid(int tank, @NotNull FluidStack stack) {
		IFluidHandler handler = owner.getRocketFluid();
		if (handler == null || tank < 0 || tank >= handler.getTanks()) {
			return false;
		}
		return handler.isFluidValid(tank, stack);
	}

	@Override
	public int fill(FluidStack resource, FluidAction action) {
		IFluidHandler handler = owner.getRocketFluid();
		if (handler == null || resource.isEmpty()) {
			return 0;
		}
		return handler.fill(resource, action);
	}

	// 输入端口不允许抽取
	@Override
	public @NotNull FluidStack drain(FluidStack resource, FluidAction action) {
		return FluidStack.EMPTY;
	}

	@Override
	public @NotNull FluidStack drain(int maxDrain, FluidAction action) {
		return FluidStack.EMPTY;
	}
}