package dev.celestiacraft.cmi.common.block.pump.water.capability;

import dev.celestiacraft.cmi.common.block.pump.water.WaterPumpBlockEntity;
import dev.celestiacraft.cmi.utils.ModResources;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

public class WaterPumpFluidCapability implements IFluidHandler {
	private final WaterPumpBlockEntity entity;

	public WaterPumpFluidCapability(WaterPumpBlockEntity entity) {
		this.entity = entity;
	}

	@Override
	public int getTanks() {
		return 1;
	}

	@Override
	public @NotNull FluidStack getFluidInTank(int amount) {
		if (entity.isStructureValid()) {
			if (entity.isOcean()) {
				return new FluidStack(ForgeRegistries.FLUIDS.getValue(ModResources.SEA_WATER), Integer.MAX_VALUE);
			}
			return new FluidStack(Fluids.WATER, Integer.MAX_VALUE);
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
		if (entity.isStructureValid()) {
			if (entity.isOcean()) {
				if (fluidStack.getFluid() == ForgeRegistries.FLUIDS.getValue(ModResources.SEA_WATER)) {
					return fluidStack;
				}
			} else if (fluidStack.getFluid() == Fluids.WATER) {
				return fluidStack;
			}
			return FluidStack.EMPTY;
		}
		return FluidStack.EMPTY;
	}

	@Override
	public @NotNull FluidStack drain(int amount, FluidAction fluidAction) {
		if (entity.isStructureValid()) {
			if (entity.isOcean()) {
				return new FluidStack(ForgeRegistries.FLUIDS.getValue(ModResources.SEA_WATER), amount);
			}
			return new FluidStack(Fluids.WATER, amount);
		}
		return FluidStack.EMPTY;
	}
}