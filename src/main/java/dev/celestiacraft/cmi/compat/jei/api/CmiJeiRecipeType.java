package dev.celestiacraft.cmi.compat.jei.api;

import dev.celestiacraft.cmi.Cmi;
import dev.celestiacraft.cmi.common.block.belt_grinder.GrindingRecipe;
import dev.celestiacraft.cmi.common.recipe.accelerator.AcceleratorRecipe;
import dev.celestiacraft.cmi.common.recipe.void_dust_collector.VoidDustCollectorRecipe;
import dev.celestiacraft.cmi.common.recipe.well.WellRecipe;
import mezz.jei.api.recipe.RecipeType;

public class CmiJeiRecipeType {
	public static final RecipeType<AcceleratorRecipe> ACCELERATOR;
	public static final RecipeType<WellRecipe> WELL;
	public static final RecipeType<GrindingRecipe> GRINDING;
	public static final RecipeType<VoidDustCollectorRecipe> VOID_DUST_COLLECTOR;

	static {
		ACCELERATOR = add("accelerator", AcceleratorRecipe.class);
		WELL = add("well", WellRecipe.class);
		GRINDING = add("grinding", GrindingRecipe.class);
		VOID_DUST_COLLECTOR = add("void_dust_collector", VoidDustCollectorRecipe.class);
	}

	private static <T> RecipeType<T> add(String name, Class<? extends T> recipeClass) {
		return RecipeType.create(Cmi.MODID, name, recipeClass);
	}
}