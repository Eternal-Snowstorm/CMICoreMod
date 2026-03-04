package dev.celestiacraft.cmi.common.recipe.freezing;

import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder;
import com.simibubi.create.foundation.recipe.IRecipeTypeInfo;
import dev.celestiacraft.cmi.common.recipe.accelerator.AcceleratorRecipe;
import dev.celestiacraft.cmi.common.register.CmiCreateRecipe;
import dev.celestiacraft.cmi.common.register.CmiRecipeType;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.RecipeWrapper;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class FreezingRecipe extends ProcessingRecipe<FreezingRecipe.FreezingWrapper> {

	public FreezingRecipe(ProcessingRecipeBuilder.ProcessingRecipeParams params) {
		super(CmiCreateRecipe.FREEZING, params);
	}

	@Override
	public boolean matches(FreezingWrapper inv, Level worldIn) {
		if (inv.isEmpty())
			return false;
		return ingredients.get(0)
				.test(inv.getItem(0));
	}

	@Override
	protected int getMaxInputCount() {
		return 1;
	}

	@Override
	protected int getMaxOutputCount() {
		return 12;
	}

	public static class FreezingWrapper extends RecipeWrapper {
		public FreezingWrapper() {
			super(new ItemStackHandler(1));
		}
	}

}