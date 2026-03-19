package dev.celestiacraft.cmi.common.register;

import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import dev.celestiacraft.cmi.Cmi;
import dev.celestiacraft.cmi.common.recipe.accelerator.AcceleratorRecipe;
import dev.celestiacraft.cmi.common.recipe.space_elevator_base.SpaceElevatorBaseRecipe;

import java.util.function.Supplier;

public class CmiRecipeSerializer {
	public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS;
	public static final Supplier<RecipeSerializer<AcceleratorRecipe>> ACCELERATOR;
	public static final Supplier<RecipeSerializer<SpaceElevatorBaseRecipe>> SPACE_ELEVATOR_BASE;

	static {
		SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, Cmi.MODID);

		ACCELERATOR = SERIALIZERS.register("accelerator", () -> AcceleratorRecipe.Serializer.INSTANCE);
		SPACE_ELEVATOR_BASE = SERIALIZERS.register("space_elevator_base", () -> SpaceElevatorBaseRecipe.Serializer.INSTANCE);
	}

	public static void register(IEventBus bus) {
		SERIALIZERS.register(bus);
		Cmi.LOGGER.info("CMI Core RecipeSerializers Registered!");
	}
}