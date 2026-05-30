package dev.celestiacraft.cmi.compat.kubejs.recipe.cdg;

import com.mojang.datafixers.util.Either;
import com.simibubi.create.content.processing.recipe.HeatCondition;
import dev.latvian.mods.kubejs.fluid.InputFluid;
import dev.latvian.mods.kubejs.fluid.OutputFluid;
import dev.latvian.mods.kubejs.item.InputItem;
import dev.latvian.mods.kubejs.item.OutputItem;
import dev.latvian.mods.kubejs.recipe.RecipeKey;
import dev.latvian.mods.kubejs.recipe.component.FluidComponents;
import dev.latvian.mods.kubejs.recipe.component.ItemComponents;
import dev.latvian.mods.kubejs.recipe.component.StringComponent;
import dev.latvian.mods.kubejs.recipe.component.TimeComponent;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchema;

public interface CdgRecipesSchema {
	RecipeKey<Either<OutputFluid, OutputItem>[]> RESULTS = FluidComponents.OUTPUT_OR_ITEM_ARRAY
			.key("results");

	RecipeKey<OutputItem[]> RESULT = ItemComponents.OUTPUT_ARRAY
			.key("results");

	RecipeKey<Either<InputFluid, InputItem>[]> INGREDIENTS = FluidComponents.INPUT_OR_ITEM_ARRAY
			.key("ingredients");

	RecipeKey<Either<InputFluid, InputItem>[]> INGREDIENTS_UNWRAPPED = new IngredientsUnwrappedComponent()
			.key("ingredients");

	RecipeKey<Long> PROCESSING_TIME_REQUIRED = TimeComponent.TICKS
			.key("processingTime")
			.optional(100L)
			.alwaysWrite();

	RecipeKey<String> MOLDNAME = StringComponent.ID
			.key("mold")
			.defaultOptional();

	RecipeKey<String> HEAT_REQUIREMENT = new StringComponent("not a valid heat condition!",
			(string) -> {
				for (HeatCondition condition : HeatCondition.values()) {
					if (condition.name().equalsIgnoreCase(string)) {
						return true;
					}
				}
				return false;
			})
			.key("heatRequirement")
			.defaultOptional()
			.allowEmpty();

	RecipeSchema BASIN = new RecipeSchema(
			CdgRecipeJS.class,
			CdgRecipeJS::new,
			RESULTS,
			INGREDIENTS_UNWRAPPED,
			PROCESSING_TIME_REQUIRED,
			HEAT_REQUIREMENT
	);

	RecipeSchema DISTILLATION = new RecipeSchema(
			CdgRecipeJS.class,
			CdgRecipeJS::new,
			RESULTS,
			INGREDIENTS_UNWRAPPED,
			PROCESSING_TIME_REQUIRED,
			HEAT_REQUIREMENT
	);

	RecipeSchema BULK = new RecipeSchema(
			CdgRecipeJS.class,
			CdgRecipeJS::new,
			RESULTS,
			INGREDIENTS_UNWRAPPED,
			PROCESSING_TIME_REQUIRED,
			HEAT_REQUIREMENT
	);

	RecipeSchema COMPRESSION = new RecipeSchema(
			CdgRecipeJS.class,
			CdgRecipeJS::new,
			RESULTS,
			INGREDIENTS,
			MOLDNAME,
			HEAT_REQUIREMENT
	);

	RecipeSchema CASTING = new RecipeSchema(
			CdgRecipeJS.class,
			CdgRecipeJS::new,
			RESULTS,
			INGREDIENTS,
			MOLDNAME
	);

	RecipeSchema HAMMERING = new RecipeSchema(
			CdgRecipeJS.class,
			CdgRecipeJS::new,
			RESULT,
			INGREDIENTS
	);

	RecipeSchema CUTTING = new RecipeSchema(
			CdgRecipeJS.class,
			CdgRecipeJS::new,
			RESULT,
			INGREDIENTS
	);
}