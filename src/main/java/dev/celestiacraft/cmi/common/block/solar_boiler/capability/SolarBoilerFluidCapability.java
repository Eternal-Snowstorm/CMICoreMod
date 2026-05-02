package dev.celestiacraft.cmi.common.block.solar_boiler.capability;

import net.minecraft.core.Direction;
import net.minecraft.tags.FluidTags;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.NotNull;

public class SolarBoilerFluidCapability {
	private final LazyOptional<IFluidHandler> handler;

	public SolarBoilerFluidCapability(FluidTank waterTank, FluidTank steamTank) {
		handler = LazyOptional.of(() -> new IFluidHandler() {
			@Override
			public int getTanks() {
				return 2;
			}

			@Override
			public @NotNull FluidStack getFluidInTank(int tank) {
				return tank == 0 ? waterTank.getFluid() : steamTank.getFluid();
			}

			@Override
			public int getTankCapacity(int tank) {
				return tank == 0 ? waterTank.getCapacity() : steamTank.getCapacity();
			}

			@Override
			public boolean isFluidValid(int tank, @NotNull FluidStack stack) {
				return tank == 0 && stack.getFluid().is(FluidTags.WATER);
			}

			// 外部只能输入水
			@Override
			public int fill(FluidStack resource, FluidAction action) {
				if (!resource.getFluid().is(FluidTags.WATER)) return 0;
				return waterTank.fill(resource, action);
			}

			// 外部只能抽蒸汽
			@Override
			public @NotNull FluidStack drain(int maxDrain, FluidAction action) {
				return steamTank.drain(maxDrain, action);
			}

			@Override
			public @NotNull FluidStack drain(FluidStack resource, FluidAction action) {
				return steamTank.drain(resource, action);
			}
		});
	}

	public LazyOptional<IFluidHandler> get(Direction side) {
		return handler;
	}

	public void invalidate() {
		handler.invalidate();
	}
}