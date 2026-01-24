package top.nebula.cmi.common.register;

import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import top.nebula.cmi.Cmi;
import top.nebula.cmi.common.recipe.accelerator.AcceleratorRecipe;

import java.util.function.Supplier;

public class ModRecipeSerializer {
	public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS;
	public static final Supplier<RecipeSerializer<AcceleratorRecipe>> ACCELERATOR;

	static {
		SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, Cmi.MODID);

		ACCELERATOR = SERIALIZERS.register("accelerator", () -> {
			return AcceleratorRecipe.Serializer.INSTANCE;
		});
	}

	public static void register(IEventBus event) {
		SERIALIZERS.register(event);
		Cmi.LOGGER.info("CMI Core RecipeSerializers Registered!");
	}
}