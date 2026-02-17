package dev.celestiacraft.cmi.compat.jei;

import com.simibubi.create.Create;
import dev.celestiacraft.cmi.compat.jei.category.*;
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
import dev.celestiacraft.cmi.Cmi;
import dev.celestiacraft.cmi.common.block.belt_grinder.GrindingRecipe;
import dev.celestiacraft.cmi.common.recipe.accelerator.AcceleratorRecipe;
import dev.celestiacraft.cmi.common.recipe.void_dust_collector.VoidDustCollectorRecipe;
import dev.celestiacraft.cmi.common.recipe.water_pump.WaterPumpRecipe;
import dev.celestiacraft.cmi.common.recipe.water_pump.WaterPumpSeaWaterRecipe;
import dev.celestiacraft.cmi.common.register.CmiBlocks;
import dev.celestiacraft.cmi.common.register.CmiCreateRecipe;
import dev.celestiacraft.cmi.compat.jei.api.CmiJeiRecipeType;
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
		registration.addRecipeCategories(GrindingCategory.builder(helper));
	}

	@Override
	public void registerRecipes(IRecipeRegistration registration) {
		RecipeManager manager = Minecraft.getInstance().level.getRecipeManager();

		List<AcceleratorRecipe> acceleratorRecipe = manager.getAllRecipesFor(AcceleratorRecipe.Type.INSTANCE);
		List<WaterPumpRecipe> waterPumpRecipe = List.of(new WaterPumpRecipe());
		List<WaterPumpSeaWaterRecipe> waterPumpSeaWaterRecipe = List.of(new WaterPumpSeaWaterRecipe());
		List<VoidDustCollectorRecipe> voidDustCollectorRecipe = List.of(new VoidDustCollectorRecipe());
		List<GrindingRecipe> grindingRecipe = manager.getAllRecipesFor(CmiCreateRecipe.GRINDING.getType());

		registration.addRecipes(CmiJeiRecipeType.ACCELERATOR, acceleratorRecipe);
		registration.addRecipes(CmiJeiRecipeType.WATER_PUMP, waterPumpRecipe);
		registration.addRecipes(CmiJeiRecipeType.SEA_WATER_PUMP, waterPumpSeaWaterRecipe);
		registration.addRecipes(CmiJeiRecipeType.VOID_DUST_COLLECTOR, voidDustCollectorRecipe);
		registration.addRecipes(CmiJeiRecipeType.GRINDING, grindingRecipe);
	}

	@Override
	public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
		IJeiHelpers helpers = registration.getJeiHelpers();

		registration.addRecipeCatalyst(
				AcceleratorCategory.ACCELERATOR_ITEM.get().getDefaultInstance(),
				CmiJeiRecipeType.ACCELERATOR
		);
		registration.addRecipeCatalyst(
				CmiBlocks.WATER_PUMP.asStack(),
				CmiJeiRecipeType.WATER_PUMP
		);
		registration.addRecipeCatalyst(
				CmiBlocks.WATER_PUMP.asStack(),
				CmiJeiRecipeType.SEA_WATER_PUMP
		);
		registration.addRecipeCatalyst(
				CmiBlocks.VOID_DUST_COLLECTOR.asStack(),
				CmiJeiRecipeType.VOID_DUST_COLLECTOR
		);
		registration.addRecipeCatalyst(
				VoidDustCollectorCategory.VOID_SPRING.get().asItem().getDefaultInstance(),
				CmiJeiRecipeType.VOID_DUST_COLLECTOR
		);
		registration.addRecipeCatalyst(
				CmiBlocks.BELT_GRINDER.asStack(),
				CmiJeiRecipeType.GRINDING
		);

		Map<String, ItemStack> createCatalysts = Map.of(
				"pressing", CmiBlocks.STEAM_HAMMER.asStack(),
				"spout_filling", CmiBlocks.ADVANCED_SPOUT.asStack()
		);

		createCatalysts.forEach((recipeId, stack) -> {
			helpers.getRecipeType(Create.asResource(recipeId))
					.ifPresent((type) -> {
						registration.addRecipeCatalyst(stack, type);
					});
		});
	}
}