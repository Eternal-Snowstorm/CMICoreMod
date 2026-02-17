package dev.celestiacraft.cmi.common.register;

import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import dev.celestiacraft.cmi.Cmi;
import dev.celestiacraft.cmi.common.recipe.accelerator.AcceleratorRecipe;

import java.util.function.Supplier;

public class CmiRecipeSerializer {
	public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS;
	public static final Supplier<RecipeSerializer<AcceleratorRecipe>> ACCELERATOR;

	static {
		SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, Cmi.MODID);

		ACCELERATOR = SERIALIZERS.register("accelerator", () -> {
			return AcceleratorRecipe.Serializer.INSTANCE;
		});
	}

	public static void register(IEventBus bus) {
		SERIALIZERS.register(bus);
		Cmi.LOGGER.info("CMI Core RecipeSerializers Registered!");
	}
}