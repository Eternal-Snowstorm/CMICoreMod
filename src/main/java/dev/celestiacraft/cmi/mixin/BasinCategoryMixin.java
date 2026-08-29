package dev.celestiacraft.cmi.mixin;

import com.simibubi.create.compat.jei.category.BasinCategory;
import com.simibubi.create.content.processing.basin.BasinRecipe;
import com.simibubi.create.content.processing.recipe.HeatCondition;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.recipe.IFocusGroup;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = BasinCategory.class, remap = false)
public class BasinCategoryMixin {
	@Inject(
			method = "setRecipe(Lmezz/jei/api/gui/builder/IRecipeLayoutBuilder;Lcom/simibubi/create/content/processing/basin/BasinRecipe;Lmezz/jei/api/recipe/IFocusGroup;)V",
			at = @At(
					value = "INVOKE",
					target = "Lcom/simibubi/create/content/processing/basin/BasinRecipe;getRequiredHeat()Lcom/simibubi/create/content/processing/recipe/HeatCondition;"
			),
			remap = false,
			cancellable = true
	)
	public void cmi$setRecipe(IRecipeLayoutBuilder builder, BasinRecipe recipe, IFocusGroup focuses, CallbackInfo info) {
		if (recipe.getRequiredHeat() == HeatCondition.valueOf("GRILLED")) {
			info.cancel();
		}
	}
}