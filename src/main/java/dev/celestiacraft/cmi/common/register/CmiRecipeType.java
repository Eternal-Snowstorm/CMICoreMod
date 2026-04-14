package dev.celestiacraft.cmi.common.register;

import dev.celestiacraft.cmi.common.recipe.fluid_burn.FluidBurnRecipe;
import dev.celestiacraft.libs.common.recipe.machine.MachineRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import dev.celestiacraft.cmi.Cmi;
import dev.celestiacraft.cmi.common.recipe.accelerator.AcceleratorRecipe;
import dev.celestiacraft.cmi.common.recipe.space_elevator_base.SpaceElevatorBaseRecipe;
import dev.celestiacraft.cmi.common.recipe.space_elevator_construction.SpaceElevatorConstructionRecipe;

import java.util.function.Supplier;

public class CmiRecipeType {
	public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES;
	public static final Supplier<RecipeType<AcceleratorRecipe>> ACCELERATOR;
	public static final Supplier<RecipeType<SpaceElevatorBaseRecipe>> SPACE_ELEVATOR_BASE;
	public static final Supplier<RecipeType<SpaceElevatorConstructionRecipe>> SPACE_ELEVATOR_CONSTRUCTION;
	public static final Supplier<RecipeType<FluidBurnRecipe>> FLUID_BURN;
	public static final Supplier<RecipeType<MachineRecipe>> TEST_COKE_OVEN;

	static {
		RECIPE_TYPES = DeferredRegister.create(ForgeRegistries.RECIPE_TYPES, Cmi.MODID);

		ACCELERATOR = register("accelerator", () -> AcceleratorRecipe.Type.INSTANCE);
		SPACE_ELEVATOR_BASE = register("space_elevator_base", () -> SpaceElevatorBaseRecipe.Type.INSTANCE);
		SPACE_ELEVATOR_CONSTRUCTION = register("space_elevator_construction", () -> SpaceElevatorConstructionRecipe.Type.INSTANCE);
		FLUID_BURN = register("fluid_burn", () -> FluidBurnRecipe.Type.INSTANCE);
		TEST_COKE_OVEN = registerSimple("test_coke_oven");
	}

	private static <T extends Recipe<?>> Supplier<RecipeType<T>> register(String path, Supplier<RecipeType<T>> supplier) {
		return RECIPE_TYPES.register(path, supplier);
	}

	private static <T extends Recipe<?>> Supplier<RecipeType<T>> registerSimple(String path) {
		return RECIPE_TYPES.register(path, () -> RecipeType.simple(Cmi.loadResource(path)));
	}

	public static void register(IEventBus bus) {
		RECIPE_TYPES.register(bus);
		Cmi.LOGGER.info("CMI Core RecipeTypes Registered!");
	}
}
