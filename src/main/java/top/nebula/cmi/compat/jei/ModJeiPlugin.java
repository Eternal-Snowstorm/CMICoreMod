package top.nebula.cmi.compat.jei;

import com.simibubi.create.Create;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeManager;
import org.jetbrains.annotations.NotNull;
import top.nebula.cmi.Cmi;
import top.nebula.cmi.common.recipe.accelerator.AcceleratorRecipe;
import top.nebula.cmi.common.recipe.waterpump.WaterPumpRecipe;
import top.nebula.cmi.common.recipe.waterpump.WaterPumpSeaWaterRecipe;
import top.nebula.cmi.common.register.ModBlocks;
import top.nebula.cmi.compat.jei.category.AcceleratorCategory;
import top.nebula.cmi.compat.jei.category.WaterPumpCategory;
import top.nebula.cmi.compat.jei.category.WaterPumpSeaWaterCategory;

import java.util.List;

@JeiPlugin
public class ModJeiPlugin implements IModPlugin {
	@Override
	public @NotNull ResourceLocation getPluginUid() {
		return Cmi.loadResource("jei_plugin");
	}

	@Override
	public void registerCategories(@NotNull IRecipeCategoryRegistration registration) {
		registration.addRecipeCategories(new AcceleratorCategory(registration.getJeiHelpers().getGuiHelper()));
		registration.addRecipeCategories(new WaterPumpCategory(registration.getJeiHelpers().getGuiHelper()));
		registration.addRecipeCategories(new WaterPumpSeaWaterCategory(registration.getJeiHelpers().getGuiHelper()));
	}

	@Override
	public void registerRecipes(IRecipeRegistration registration) {
		RecipeManager manager = Minecraft.getInstance().level.getRecipeManager();

		List<AcceleratorRecipe> acceleratorRecipe = manager.getAllRecipesFor(AcceleratorRecipe.Type.INSTANCE);
		List<WaterPumpRecipe> waterPumpRecipe = List.of(new WaterPumpRecipe());
		List<WaterPumpSeaWaterRecipe> waterPumpSeaWaterRecipe = List.of(new WaterPumpSeaWaterRecipe());

		registration.addRecipes(AcceleratorCategory.ACCELERATOR_TYPE, acceleratorRecipe);
		registration.addRecipes(WaterPumpCategory.WATER_PUMP_TYPE, waterPumpRecipe);
		registration.addRecipes(WaterPumpSeaWaterCategory.WATER_PUMP_SEA_WATER_TYPE, waterPumpSeaWaterRecipe);
	}

	@Override
	public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
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
		registration.getJeiHelpers().getRecipeType(Create.asResource("pressing"))
				.ifPresent((type) -> {
					registration.addRecipeCatalyst(ModBlocks.STEAM_HAMMER.asStack(), type);
				});
		registration.getJeiHelpers().getRecipeType(Create.asResource("spout_filling"))
				.ifPresent((type) -> {
					registration.addRecipeCatalyst(ModBlocks.FAST_SPOUT.asStack(), type);
				});
	}
}