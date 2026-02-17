package dev.celestiacraft.cmi.compat.kubejs.recipe;

import dev.latvian.mods.kubejs.item.InputItem;
import dev.latvian.mods.kubejs.recipe.RecipeKey;
import dev.latvian.mods.kubejs.recipe.component.*;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchema;
import net.minecraft.world.level.block.Block;

public interface AcceleratorSchema {
	RecipeKey<RecipeComponentBuilderMap[]> OUTPUT = output().key("output");
	RecipeKey<InputItem[]> INPUT = ItemComponents.INPUT_ARRAY.key("input").defaultOptional();
	RecipeKey<Block> TARGET = BlockComponent.OUTPUT.key("target").defaultOptional();

	RecipeSchema SCHEMA = new RecipeSchema(OUTPUT, INPUT, TARGET);

	private static ArrayRecipeComponent<RecipeComponentBuilderMap> output() {
		return new RecipeComponentBuilder(2)
				.add(BlockComponent.OUTPUT.key("id"))
				.add(NumberComponent.DOUBLE.key("chance"))
				.inputRole()
				.asArray();
	}
}