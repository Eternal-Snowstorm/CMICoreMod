package dev.celestiacraft.cmi.compat.steam_powered.block;

import dev.celestiacraft.cmi.compat.steam_powered.block.fluid_burner.FluidBurnerBlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;

public class FluidBurnerFluidHandler implements IFluidHandler {
	private final FluidBurnerBlockEntity entity;

	public FluidBurnerFluidHandler(FluidBurnerBlockEntity entity) {
		this.entity = entity;
	}

	@Override
	public int getTanks() {
		return 1;
	}

	@Override
	public @NotNull FluidStack getFluidInTank(int tank) {
		return entity.fluid;
	}

	@Override
	public int getTankCapacity(int tank) {
		return entity.getFluidTankCapacity();
	}

	@Override
	public boolean isFluidValid(int tank, @NotNull FluidStack stack) {
		return entity.findRecipe(stack) != null;
	}

	@Override
	public int fill(FluidStack resource, FluidAction action) {
		if (resource.isEmpty() || !isFluidValid(0, resource)) {
			return 0;
		}

		if (!entity.fluid.isEmpty() && !entity.fluid.isFluidEqual(resource)) {
			return 0;
		}

		int fillAmount = Math.min(entity.getFluidTankCapacity() - entity.fluid.getAmount(), resource.getAmount());

		if (action.execute() && fillAmount > 0) {
			if (entity.fluid.isEmpty()) {
				entity.fluid = new FluidStack(resource, fillAmount);
			} else {
				entity.fluid.grow(fillAmount);
			}
			entity.setChanged();
		}

		return fillAmount;
	}

	@Override
	public @NotNull FluidStack drain(int maxDrain, FluidAction action) {
		if (entity.fluid.isEmpty()) {
			return FluidStack.EMPTY;
		}

		int drained = Math.min(maxDrain, entity.fluid.getAmount());
		FluidStack result = new FluidStack(entity.fluid, drained);

		if (action.execute()) {
			entity.fluid.shrink(drained);
			if (entity.fluid.getAmount() <= 0) {
				entity.fluid = FluidStack.EMPTY;
			}
			entity.setChanged();
		}

		return result;
	}

	@Override
	public @NotNull FluidStack drain(FluidStack resource, FluidAction action) {
		if (!entity.fluid.isFluidEqual(resource)) {
			return FluidStack.EMPTY;
		}
		return drain(resource.getAmount(), action);
	}

	public void readFromNBT(CompoundTag tag) {
		entity.fluid = FluidStack.loadFluidStackFromNBT(tag);
	}

	public CompoundTag writeToNBT() {
		CompoundTag tag = new CompoundTag();
		entity.fluid.writeToNBT(tag);
		return tag;
	}
}