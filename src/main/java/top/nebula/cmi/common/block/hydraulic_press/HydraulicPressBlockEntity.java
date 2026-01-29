package top.nebula.cmi.common.block.hydraulic_press;

import com.simibubi.create.content.kinetics.press.MechanicalPressBlockEntity;
import com.simibubi.create.content.kinetics.press.PressingRecipe;
import com.simibubi.create.foundation.item.ItemHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
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
import top.nebula.cmi.common.tag.ModFluidTags;

import java.util.Optional;

public class HydraulicPressBlockEntity extends MechanicalPressBlockEntity {
	private static final int STEAM_CAPACITY = 10000;
	public static final int STEAM_COST = 1000;

	public HydraulicPressBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	private FluidStack steam = FluidStack.EMPTY;
	private final IFluidHandler fluidHandler = new IFluidHandler() {
		@Override
		public int getTanks() {
			return 1;
		}

		@Override
		public @NotNull FluidStack getFluidInTank(int tank) {
			return steam;
		}

		@Override
		public int getTankCapacity(int tank) {
			return STEAM_CAPACITY;
		}

		@Override
		public boolean isFluidValid(int tank, @NotNull FluidStack stack) {
			return stack.getFluid().is(ModFluidTags.STEAM);
		}

		@Override
		public int fill(FluidStack resource, FluidAction action) {
			if (!isFluidValid(0, resource) || resource.isEmpty()) {
				return 0;
			}

			if (steam.isEmpty()) {
				int fill = Math.min(resource.getAmount(), STEAM_CAPACITY);
				if (action.execute()) {
					steam = new FluidStack(resource.getFluid(), fill);
					setChanged();
				}
				return fill;
			}

			if (!steam.isFluidEqual(resource)) {
				return 0;
			}

			int fill = Math.min(resource.getAmount(), STEAM_CAPACITY - steam.getAmount());
			if (fill > 0 && action.execute()) {
				steam.grow(fill);
				setChanged();
			}

			return fill;
		}

		@Override
		public @NotNull FluidStack drain(FluidStack resource, FluidAction action) {
			return FluidStack.EMPTY;
		}

		@Override
		public @NotNull FluidStack drain(int maxDrain, FluidAction action) {
			return FluidStack.EMPTY;
		}
	};

	private boolean hasEnoughSteam() {
		return !steam.isEmpty() && steam.getAmount() >= STEAM_COST;
	}

	private void consumeSteam() {
		if (!steam.isEmpty()) {
			steam.shrink(STEAM_COST);
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
		NonNullList<Ingredient> ingredients = recipe.getIngredients();
		return (ingredients.size() == 4 || ingredients.size() == 9) && ItemHelper.matchAllIngredients(ingredients);
	}

	@Override
	public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction side) {
		if (capability == ForgeCapabilities.FLUID_HANDLER) {
			return LazyOptional.of(() -> {
				return fluidHandler;
			}).cast();
		}
		return super.getCapability(capability, side);
	}
}