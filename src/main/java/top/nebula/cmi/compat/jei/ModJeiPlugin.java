package top.nebula.cmi.compat.jei;

import com.simibubi.create.Create;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;
import org.jetbrains.annotations.NotNull;
import top.nebula.cmi.Cmi;
import top.nebula.cmi.common.recipe.accelerator.AcceleratorRecipe;
import top.nebula.cmi.common.recipe.void_dust_collector.VoidDustCollectorRecipe;
import top.nebula.cmi.common.recipe.water_pump.WaterPumpRecipe;
import top.nebula.cmi.common.recipe.water_pump.WaterPumpSeaWaterRecipe;
import top.nebula.cmi.common.register.ModBlocks;
import top.nebula.cmi.compat.jei.category.*;

import java.util.List;
import java.util.Map;

@JeiPlugin
public class ModJeiPlugin implements IModPlugin {
	@Override
	public @NotNull ResourceLocation getPluginUid() {
		return Cmi.loadResource("jei_plugin");
	}

	@Override
	public void registerCategories(@NotNull IRecipeCategoryRegistration registration) {
		IGuiHelper helper = registration.getJeiHelpers().getGuiHelper();

		registration.addRecipeCategories(AcceleratorCategory.builder(helper));
		registration.addRecipeCategories(WaterPumpCategory.builder(helper));
		registration.addRecipeCategories(WaterPumpSeaWaterCategory.builder(helper));
		registration.addRecipeCategories(VoidDustCollectorCategory.builder(helper));
	}

	@Override
	public void registerRecipes(IRecipeRegistration registration) {
		RecipeManager manager = Minecraft.getInstance().level.getRecipeManager();

		List<AcceleratorRecipe> acceleratorRecipe = manager.getAllRecipesFor(AcceleratorRecipe.Type.INSTANCE);
		List<WaterPumpRecipe> waterPumpRecipe = List.of(new WaterPumpRecipe());
		List<WaterPumpSeaWaterRecipe> waterPumpSeaWaterRecipe = List.of(new WaterPumpSeaWaterRecipe());
		List<VoidDustCollectorRecipe> voidDustCollectorRecipe = List.of(new VoidDustCollectorRecipe());

		registration.addRecipes(AcceleratorCategory.ACCELERATOR_TYPE, acceleratorRecipe);
		registration.addRecipes(WaterPumpCategory.WATER_PUMP_TYPE, waterPumpRecipe);
		registration.addRecipes(WaterPumpSeaWaterCategory.WATER_PUMP_SEA_WATER_TYPE, waterPumpSeaWaterRecipe);
		registration.addRecipes(VoidDustCollectorCategory.VOID_DUST_COLLECTOR_TYPE, voidDustCollectorRecipe);
	}

	@Override
	public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
		IJeiHelpers helpers = registration.getJeiHelpers();

		registration.addRecipeCatalyst(
				AcceleratorCategory.ACCELERATOR_ITEM.get().getDefaultInstance(),
				AcceleratorCategory.ACCELERATOR_TYPE
		);
		registration.addRecipeCatalyst(
				ModBlocks.WATER_PUMP.asStack(),
				WaterPumpCategory.WATER_PUMP_TYPE
		);
		registration.addRecipeCatalyst(
				ModBlocks.WATER_PUMP.asStack(),
				WaterPumpSeaWaterCategory.WATER_PUMP_SEA_WATER_TYPE
		);
		registration.addRecipeCatalyst(
				ModBlocks.VOID_DUST_COLLECTOR.asStack(),
				VoidDustCollectorCategory.VOID_DUST_COLLECTOR_TYPE
		);

		Map<String, ItemStack> createCatalysts = Map.of(
				"pressing", ModBlocks.STEAM_HAMMER.asStack(),
				"spout_filling", ModBlocks.ADVANCED_SPOUT.asStack()
		);

		createCatalysts.forEach((recipeId, stack) -> {
			helpers.getRecipeType(Create.asResource(recipeId))
					.ifPresent((type) -> {
						registration.addRecipeCatalyst(stack, type);
					});
		});
	}
}