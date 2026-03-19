package dev.celestiacraft.cmi.compat.kubejs.recipe;

import dev.latvian.mods.kubejs.item.InputItem;
import dev.latvian.mods.kubejs.recipe.RecipeKey;
import dev.latvian.mods.kubejs.recipe.component.ItemComponents;
import dev.latvian.mods.kubejs.recipe.component.StringComponent;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchema;

public interface SpaceElevatorBaseSchema {
	RecipeKey<String> DIMENSION = StringComponent.NON_BLANK.key("dimension");
	RecipeKey<InputItem[]> INPUT = ItemComponents.INPUT_ARRAY.key("ingredients").defaultOptional();

	RecipeSchema SCHEMA = new RecipeSchema(DIMENSION, INPUT);
}