package dev.celestiacraft.cmi.common.block.steam_hammer.capability;

import dev.celestiacraft.cmi.common.block.steam_hammer.SteamHammerBlockEntity;
import dev.celestiacraft.cmi.tags.CmiFluidTags;
import lombok.AllArgsConstructor;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;

@AllArgsConstructor
public class SteamHammerFluid implements IFluidHandler {
	private SteamHammerBlockEntity entity;

	@Override
	public int getTanks() {
		return 1;
	}

	@Override
	public @NotNull FluidStack getFluidInTank(int tank) {
		return entity.getSteam();
	}

	@Override
	public int getTankCapacity(int tank) {
		return SteamHammerBlockEntity.STEAM_CAPACITY;
	}

	@Override
	public boolean isFluidValid(int tank, @NotNull FluidStack stack) {
		return stack.getFluid().is(CmiFluidTags.STEAM);
	}

	@Override
	public int fill(FluidStack stack, FluidAction action) {
		if (!isFluidValid(0, stack) || stack.isEmpty()) {
			return 0;
		}

		if (entity.getSteam().isEmpty()) {
			int fill = Math.min(stack.getAmount(), SteamHammerBlockEntity.STEAM_CAPACITY);
			if (action.execute()) {
				entity.setSteam(new FluidStack(stack.getFluid(), fill));
				entity.setChanged();
			}
			return fill;
		}

		if (!entity.getSteam().isFluidEqual(stack)) {
			return 0;
		}

		int fill = Math.min(stack.getAmount(), SteamHammerBlockEntity.STEAM_CAPACITY - entity.getSteam().getAmount());
		if (fill > 0 && action.execute()) {
			entity.getSteam().grow(fill);
			entity.setChanged();
		}

		return fill;
	}

	@Override
	public @NotNull FluidStack drain(FluidStack stack, FluidAction action) {
		return FluidStack.EMPTY;
	}

	@Override
	public @NotNull FluidStack drain(int maxDrain, FluidAction action) {
		return FluidStack.EMPTY;
	}
}