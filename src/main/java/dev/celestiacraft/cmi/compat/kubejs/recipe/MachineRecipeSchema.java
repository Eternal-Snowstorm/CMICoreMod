package dev.celestiacraft.cmi.compat.kubejs.recipe;

import dev.latvian.mods.kubejs.recipe.schema.RecipeSchema;

public interface MachineRecipeSchema {
	RecipeSchema SCHEMA = new RecipeSchema(MachineRecipeJS.class, MachineRecipeJS::new);
}