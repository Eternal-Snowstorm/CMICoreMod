package dev.celestiacraft.cmi.mixin.kubejs;

import dev.celestiacraft.cmi.compat.kubejs.modify.ingredient.IIngredientUtils;
import dev.latvian.mods.kubejs.bindings.IngredientWrapper;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(IngredientWrapper.class)
public interface IngredientWrapperMixin extends IIngredientUtils {
}