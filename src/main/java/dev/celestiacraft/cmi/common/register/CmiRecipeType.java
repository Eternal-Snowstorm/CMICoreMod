package dev.celestiacraft.cmi.common.register;

import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import dev.celestiacraft.cmi.Cmi;
import dev.celestiacraft.cmi.common.recipe.accelerator.AcceleratorRecipe;

import java.util.function.Supplier;

public class CmiRecipeType {
	public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES;
	public static final Supplier<RecipeType<AcceleratorRecipe>> ACCELERATOR;

	static {
		RECIPE_TYPES = DeferredRegister.create(ForgeRegistries.RECIPE_TYPES, Cmi.MODID);

		ACCELERATOR = RECIPE_TYPES.register("accelerator", () -> {
			return AcceleratorRecipe.Type.INSTANCE;
		});
	}

	public static void register(IEventBus bus) {
		RECIPE_TYPES.register(bus);
		Cmi.LOGGER.info("CMI Core RecipeTypes Registered!");
	}
}