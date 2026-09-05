package dev.celestiacraft.cmi.common.block.steam_hammer;

import com.simibubi.create.content.kinetics.press.MechanicalPressBlockEntity;
import com.simibubi.create.content.kinetics.press.PressingRecipe;
import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import com.simibubi.create.content.processing.basin.BasinRecipe;
import com.simibubi.create.foundation.item.ItemHelper;
import dev.celestiacraft.cmi.common.block.steam_hammer.capability.SteamHammerFluid;
import dev.celestiacraft.cmi.config.common.SteamHammerConfig;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class SteamHammerBlockEntity extends MechanicalPressBlockEntity {
	public static final int STEAM_CAPACITY = SteamHammerConfig.STEAM_CAPACITY.get();
	@Getter
	@Setter
	private FluidStack steam = FluidStack.EMPTY;

	private final IFluidHandler fluidHandler = new SteamHammerFluid(this);

	public SteamHammerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	private boolean hasEnoughSteam() {
		return !steam.isEmpty() && steam.getAmount() >= SteamHammerConfig.STEAM_CONSUMPTION.get();
	}

	private void consumeSteam() {
		if (!steam.isEmpty()) {
			steam.shrink(SteamHammerConfig.STEAM_CONSUMPTION.get());
			if (steam.isEmpty()) {
				steam = FluidStack.EMPTY;
			}
			setChanged();
		}
	}

	@Override
	public boolean canProcessInBulk() {
		return hasEnoughSteam();
	}

	@Override
	public void onPressingCompleted() {
		super.onPressingCompleted();
		consumeSteam();
	}

	@Override
	public boolean tryProcessInBasin(boolean simulate) {
		if (!hasEnoughSteam()) {
			return false;
		}
		return super.tryProcessInBasin(simulate);
	}

	@Override
	protected boolean updateBasin() {
		if (!hasEnoughSteam()) {
			return true;
		}
		return super.updateBasin();
	}

	@Override
	protected void applyBasinRecipe() {
		super.applyBasinRecipe();

		/*
		 * 批量处理: 只要剩余输入足够, 就持续应用匹配的盆配方,
		 * 与传送带/置物台上的批量压制处理方式保持一致
		 */
		if (!canProcessInBulk() || currentRecipe == null) {
			return;
		}

		BasinBlockEntity basin = getBasin().orElse(null);
		if (basin == null) {
			return;
		}

		Recipe<?> recipe = currentRecipe;
		for (int i = 0; i < 256 && matchBasinRecipe((Recipe<Container>) recipe); i++) {
			if (!BasinRecipe.apply(basin, recipe)) {
				break;
			}
			basin.notifyChangeOfContents();
		}
	}

	@Override
	public Optional<PressingRecipe> getRecipe(ItemStack item) {
		if (!hasEnoughSteam()) {
			return Optional.empty();
		}
		return super.getRecipe(item);
	}

	public static <C extends Container> boolean canCompress(Recipe<C> recipe) {
		if (!(recipe instanceof CraftingRecipe)) {
			return false;
		}
		NonNullList ingredients = recipe.getIngredients();
		return (ingredients.size() == 4 || ingredients.size() == 9) && ItemHelper.matchAllIngredients(ingredients);
	}

	private LazyOptional<IFluidHandler> fluidCapability = LazyOptional.of(() -> fluidHandler);

	@Override
	public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction side) {
		if (capability == ForgeCapabilities.FLUID_HANDLER) {
			return fluidCapability.cast();
		}
		return super.getCapability(capability, side);
	}

	@Override
	public void invalidateCaps() {
		super.invalidateCaps();
		fluidCapability.invalidate();
	}

	@Override
	public void reviveCaps() {
		super.reviveCaps();
		fluidCapability = LazyOptional.of(() -> fluidHandler);
	}
}