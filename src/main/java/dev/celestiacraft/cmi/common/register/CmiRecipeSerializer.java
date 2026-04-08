package dev.celestiacraft.cmi.common.register;

import dev.celestiacraft.cmi.Cmi;
import dev.celestiacraft.cmi.common.recipe.accelerator.AcceleratorRecipe;
import dev.celestiacraft.cmi.common.recipe.fluid_burn.FluidBurnRecipe;
import dev.celestiacraft.cmi.common.recipe.machine.MachineRecipe;
import dev.celestiacraft.cmi.common.recipe.machine.MachineRecipeSerializer;
import dev.celestiacraft.cmi.common.recipe.space_elevator_base.SpaceElevatorBaseRecipe;
import dev.celestiacraft.cmi.common.recipe.space_elevator_construction.SpaceElevatorConstructionRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

public class CmiRecipeSerializer {
	public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS;
	public static final Supplier<RecipeSerializer<AcceleratorRecipe>> ACCELERATOR;
	public static final Supplier<RecipeSerializer<SpaceElevatorBaseRecipe>> SPACE_ELEVATOR_BASE;
	public static final Supplier<RecipeSerializer<SpaceElevatorConstructionRecipe>> SPACE_ELEVATOR_CONSTRUCTION;
	public static final Supplier<RecipeSerializer<FluidBurnRecipe>> FLUID_BURN;
	public static final Supplier<RecipeSerializer<MachineRecipe>> TEST_COKE_OVEN;

	static {
		SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, Cmi.MODID);

		ACCELERATOR = register("accelerator", () -> AcceleratorRecipe.Serializer.INSTANCE);
		SPACE_ELEVATOR_BASE = register("space_elevator_base", () -> SpaceElevatorBaseRecipe.Serializer.INSTANCE);
		SPACE_ELEVATOR_CONSTRUCTION = register("space_elevator_construction", () -> SpaceElevatorConstructionRecipe.Serializer.INSTANCE);
		FLUID_BURN = register("fluid_burn", () -> FluidBurnRecipe.Serializer.INSTANCE);
		TEST_COKE_OVEN = register("test_coke_oven", () -> new MachineRecipeSerializer(Cmi.loadResource("test_coke_oven")));
	}

	private static <T extends Recipe<?>> Supplier<RecipeSerializer<T>> register(String path, Supplier<RecipeSerializer<T>> supplier) {
		return SERIALIZERS.register(path, supplier);
	}

	public static void register(IEventBus bus) {
		SERIALIZERS.register(bus);
		Cmi.LOGGER.info("CMI Core RecipeSerializers Registered!");
	}
}
