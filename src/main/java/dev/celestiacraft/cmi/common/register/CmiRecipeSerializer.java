package dev.celestiacraft.cmi.common.register;

import dev.celestiacraft.cmi.Cmi;
import dev.celestiacraft.cmi.common.recipe.accelerator.AcceleratorRecipe;
import dev.celestiacraft.cmi.common.recipe.fluid_burn.FluidBurnRecipe;
import dev.celestiacraft.cmi.common.recipe.space_elevator_base.SpaceElevatorBaseRecipe;
import dev.celestiacraft.cmi.common.recipe.space_elevator_construction.SpaceElevatorConstructionRecipe;
import dev.celestiacraft.libs.common.recipe.machine.MachineRecipe;
import dev.celestiacraft.libs.common.recipe.machine.MachineRecipeSerializer;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class CmiRecipeSerializer {
	public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS;
	public static final RegistryObject<RecipeSerializer<AcceleratorRecipe>> ACCELERATOR;
	public static final RegistryObject<RecipeSerializer<SpaceElevatorBaseRecipe>> SPACE_ELEVATOR_BASE;
	public static final RegistryObject<RecipeSerializer<SpaceElevatorConstructionRecipe>> SPACE_ELEVATOR_CONSTRUCTION;
	public static final RegistryObject<RecipeSerializer<FluidBurnRecipe>> FLUID_BURN;
	public static final RegistryObject<RecipeSerializer<MachineRecipe>> TEST_COKE_OVEN;

	static {
		SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, Cmi.MODID);

		ACCELERATOR = register("accelerator", AcceleratorRecipe.Serializer.INSTANCE);
		SPACE_ELEVATOR_BASE = register("space_elevator_base", SpaceElevatorBaseRecipe.Serializer.INSTANCE);
		SPACE_ELEVATOR_CONSTRUCTION = register("space_elevator_construction", SpaceElevatorConstructionRecipe.Serializer.INSTANCE);
		FLUID_BURN = register("fluid_burn", FluidBurnRecipe.Serializer.INSTANCE);
		TEST_COKE_OVEN = register("test_coke_oven", new MachineRecipeSerializer(Cmi.loadResource("test_coke_oven")));
	}

	private static <T extends Recipe<?>> RegistryObject<RecipeSerializer<T>> register(String path, RecipeSerializer<T> serializer) {
		return SERIALIZERS.register(path, () -> serializer);
	}

	public static void register(IEventBus bus) {
		SERIALIZERS.register(bus);
		Cmi.LOGGER.info("{} RecipeSerializers Registered!", Cmi.NAME);
	}
}