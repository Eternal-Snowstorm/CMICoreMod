package dev.celestiacraft.cmi.compat.kubejs.recipe;

import dev.latvian.mods.kubejs.fluid.InputFluid;
import dev.latvian.mods.kubejs.fluid.OutputFluid;
import dev.latvian.mods.kubejs.recipe.RecipeKey;
import dev.latvian.mods.kubejs.recipe.component.NumberComponent;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchema;

/**
 * Neo ECO AE {@code neoecoae:cooling} 配方。
 *
 * <p>对应 {@code cn.dancingsnow.neoecoae.recipe.CoolingRecipe} 的 JSON 结构：
 * {@code input}（带数量的流体谓词）、{@code output}（可为空流体）、{@code coolant}、
 * {@code max_overclock}（可省略，默认 0）。</p>
 */
public interface NecoCoolingSchema {
	RecipeKey<InputFluid> INPUT = NecoRecipeComponents.ID_COMPAT_INPUT_FLUID.key("input");
	RecipeKey<Integer> COOLANT = NumberComponent.intRange(1, Integer.MAX_VALUE).key("coolant");
	RecipeKey<OutputFluid> OUTPUT = NecoRecipeComponents.ID_COMPAT_OUTPUT_FLUID.key("output").defaultOptional();
	RecipeKey<Integer> MAX_OVERCLOCK = NumberComponent.intRange(0, Integer.MAX_VALUE).key("max_overclock").optional(0).preferred("maxOverclock");

	RecipeSchema SCHEMA = new RecipeSchema(INPUT, COOLANT, OUTPUT, MAX_OVERCLOCK)
			.constructor(INPUT, COOLANT, OUTPUT, MAX_OVERCLOCK)
			.constructor(INPUT, COOLANT, OUTPUT)
			.constructor(INPUT, COOLANT);
}
