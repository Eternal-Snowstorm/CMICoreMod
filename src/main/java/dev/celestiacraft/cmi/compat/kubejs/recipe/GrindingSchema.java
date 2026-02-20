package dev.celestiacraft.cmi.compat.kubejs.recipe;

import dev.latvian.mods.kubejs.item.InputItem;
import dev.latvian.mods.kubejs.item.OutputItem;
import dev.latvian.mods.kubejs.recipe.RecipeKey;
import dev.latvian.mods.kubejs.recipe.component.*;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchema;

public interface GrindingSchema {
	RecipeKey<OutputItem[]> RESULTS = ItemComponents.OUTPUT_ARRAY.key("results");
	RecipeKey<InputItem[]> INPUT = ItemComponents.INPUT_ARRAY.key("ingredients");
	RecipeKey<Double> PROCESSING_TIME = NumberComponent.DOUBLE.key("processingTime").optional(20d);

	RecipeSchema SCHEMA = new RecipeSchema(RESULTS, INPUT, PROCESSING_TIME);
}