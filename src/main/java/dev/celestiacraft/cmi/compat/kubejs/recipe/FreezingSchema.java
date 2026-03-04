package dev.celestiacraft.cmi.compat.kubejs.recipe;

import dev.latvian.mods.kubejs.item.InputItem;
import dev.latvian.mods.kubejs.item.OutputItem;
import dev.latvian.mods.kubejs.recipe.RecipeKey;
import dev.latvian.mods.kubejs.recipe.component.ItemComponents;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchema;

public interface FreezingSchema {
	RecipeKey<OutputItem[]> RESULTS = ItemComponents.OUTPUT_ARRAY.key("results");
	RecipeKey<InputItem[]> INPUT = ItemComponents.INPUT_ARRAY.key("ingredients");

	RecipeSchema SCHEMA = new RecipeSchema(RESULTS, INPUT);
}