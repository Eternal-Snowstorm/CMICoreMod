package top.nebula.cmi.common.register;

import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import top.nebula.cmi.Cmi;
import top.nebula.cmi.common.recipe.accelerator.AcceleratorRecipe;

import java.util.function.Supplier;

public class ModRecipeType {
	public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES;
	public static final Supplier<RecipeType<AcceleratorRecipe>> ACCELERATOR;

	static {
		RECIPE_TYPES = DeferredRegister.create(ForgeRegistries.RECIPE_TYPES, Cmi.MODID);

		ACCELERATOR = RECIPE_TYPES.register("accelerator", () -> {
			return AcceleratorRecipe.Type.INSTANCE;
		});
	}

	public static void register(IEventBus event) {
		RECIPE_TYPES.register(event);
		Cmi.LOGGER.info("CMI Core RecipeTypes Registered!");
	}
}