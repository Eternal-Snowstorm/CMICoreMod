package dev.celestiacraft.cmi.common.recipe.freezing;

import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder;
import dev.celestiacraft.cmi.common.register.CmiCreateRecipe;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.RecipeWrapper;

import javax.annotation.Nonnull;

public class FreezingRecipe extends ProcessingRecipe<FreezingRecipe.Wrapper> {
	public static class Wrapper extends RecipeWrapper {
		public Wrapper() {
			super(new ItemStackHandler(1));
		}
	}
	public FreezingRecipe(ProcessingRecipeBuilder.ProcessingRecipeParams params) {
		super(CmiCreateRecipe.FREEZING, params);
	}

	@Override
	public boolean matches(@Nonnull Wrapper wrapper, @Nonnull Level level) {
		if (wrapper.isEmpty()) {
			return false;
		}
		return ingredients.get(0).test(wrapper.getItem(0));
	}

	@Override
	protected int getMaxInputCount() {
		return 1;
	}

	@Override
	protected int getMaxOutputCount() {
		return 12;
	}
}