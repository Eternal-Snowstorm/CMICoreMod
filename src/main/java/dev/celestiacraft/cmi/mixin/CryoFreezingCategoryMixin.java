package dev.celestiacraft.cmi.mixin;

import earth.terrarium.adastra.common.compat.jei.categories.CryoFreezingCategory;
import earth.terrarium.adastra.common.recipes.machines.CryoFreezingRecipe;
import earth.terrarium.botarium.common.fluid.base.FluidHolder;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CryoFreezingCategory.class)
public abstract class CryoFreezingCategoryMixin {
	@Inject(
			method = "setRecipe*",
			at = @At("TAIL"),
			remap = false
	)
	private void cmi$setRecipe(IRecipeLayoutBuilder builder, CryoFreezingRecipe recipe, IFocusGroup focuses, CallbackInfo info) {
		FluidHolder result = recipe.result();
		if (result == null || result.isEmpty()) {
			return;
		}

		builder.addInvisibleIngredients(RecipeIngredientRole.OUTPUT)
				.addFluidStack(result.getFluid(), result.getFluidAmount());
	}
}