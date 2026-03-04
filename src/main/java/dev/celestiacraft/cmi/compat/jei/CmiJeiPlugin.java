package dev.celestiacraft.cmi.compat.jei;

import com.simibubi.create.AllFluids;
import com.simibubi.create.AllItems;
import com.simibubi.create.Create;
import com.simibubi.create.compat.jei.*;
import com.simibubi.create.compat.jei.category.CreateRecipeCategory;
import com.simibubi.create.compat.jei.category.ProcessingViaFanCategory;
import com.simibubi.create.content.equipment.blueprint.BlueprintScreen;
import com.simibubi.create.content.fluids.potion.PotionFluid;
import com.simibubi.create.content.logistics.filter.AbstractFilterScreen;
import com.simibubi.create.content.redstone.link.controller.LinkedControllerScreen;
import com.simibubi.create.content.trains.schedule.ScheduleScreen;
import com.simibubi.create.foundation.gui.menu.AbstractSimiContainerScreen;
import com.simibubi.create.foundation.recipe.IRecipeTypeInfo;
import com.simibubi.create.foundation.utility.Lang;
import com.simibubi.create.infrastructure.config.AllConfigs;
import com.simibubi.create.infrastructure.config.CRecipes;
import dev.celestiacraft.cmi.common.recipe.fan_processig.freezing.FreezingRecipe;
import dev.celestiacraft.cmi.compat.jei.category.*;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.forge.ForgeTypes;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.helpers.IPlatformFluidHelper;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.registration.*;
import mezz.jei.api.runtime.IIngredientManager;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.ItemLike;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import org.jetbrains.annotations.NotNull;
import dev.celestiacraft.cmi.Cmi;
import dev.celestiacraft.cmi.common.block.belt_grinder.GrindingRecipe;
import dev.celestiacraft.cmi.common.recipe.accelerator.AcceleratorRecipe;
import dev.celestiacraft.cmi.common.recipe.void_dust_collector.VoidDustCollectorRecipe;
import dev.celestiacraft.cmi.common.recipe.water_pump.WaterPumpRecipe;
import dev.celestiacraft.cmi.common.recipe.water_pump.WaterPumpSeaWaterRecipe;
import dev.celestiacraft.cmi.common.register.CmiBlock;
import dev.celestiacraft.cmi.common.register.CmiCreateRecipe;
import dev.celestiacraft.cmi.compat.jei.api.CmiJeiRecipeType;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.List;
import java.util.Map;

@JeiPlugin
public class CmiJeiPlugin implements IModPlugin {
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
				CmiBlock.ACCELERATOR.asItem().getDefaultInstance(),
				CmiJeiRecipeType.ACCELERATOR
		);
		registration.addRecipeCatalyst(
				CmiBlock.WATER_PUMP.asStack(),
				CmiJeiRecipeType.WATER_PUMP
		);
		registration.addRecipeCatalyst(
				CmiBlock.WATER_PUMP.asStack(),
				CmiJeiRecipeType.SEA_WATER_PUMP
		);
		registration.addRecipeCatalyst(
				CmiBlock.VOID_DUST_COLLECTOR.asStack(),
				CmiJeiRecipeType.VOID_DUST_COLLECTOR
		);
		registration.addRecipeCatalyst(
				VoidDustCollectorCategory.VOID_SPRING.asItem().getDefaultInstance(),
				CmiJeiRecipeType.VOID_DUST_COLLECTOR
		);
		registration.addRecipeCatalyst(
				CmiBlock.BELT_GRINDER.asStack(),
				CmiJeiRecipeType.GRINDING
		);

		Map<String, ItemStack> createCatalysts = Map.of(
				"pressing", CmiBlock.STEAM_HAMMER.asStack(),
				"spout_filling", CmiBlock.ADVANCED_SPOUT.asStack()
		);

		createCatalysts.forEach((recipeId, stack) -> {
			helpers.getRecipeType(Create.asResource(recipeId), Recipe.class)
					.ifPresent((type) -> {
						registration.addRecipeCatalyst(stack, type);
					});
		});
	}

	public static class CmiFanJeiPlugin implements IModPlugin {

		private static final ResourceLocation ID = Create.asResource("jei_plugin");

		private final List<CreateRecipeCategory<?>> allCategories = new ArrayList<>();
		private IIngredientManager ingredientManager;

		private void loadCategories() {
			allCategories.clear();

			CreateRecipeCategory<?>

					freezing = builder(FreezingRecipe.class)
					.addTypedRecipes(CmiCreateRecipe.FREEZING)
					.catalystStack(ProcessingViaFanCategory.getFan("fan_freezing"))
					.doubleItemIcon(AllItems.PROPELLER.get(), Items.POWDER_SNOW_BUCKET)
					.emptyBackground(178, 72)
					.build("fan_freezing", FanFreezingCategory::new);

		}

		private <T extends Recipe<?>> CmiFanJeiPlugin.CategoryBuilder<T> builder(Class<? extends T> recipeClass) {
			return new CmiFanJeiPlugin.CategoryBuilder<>(recipeClass);
		}

		@Override
		@Nonnull
		public ResourceLocation getPluginUid() {
			return ID;
		}

		@Override
		public void registerCategories(IRecipeCategoryRegistration registration) {
			loadCategories();
			registration.addRecipeCategories(allCategories.toArray(IRecipeCategory[]::new));
		}

		@Override
		public void registerRecipes(IRecipeRegistration registration) {
			ingredientManager = registration.getIngredientManager();

			allCategories.forEach(c -> c.registerRecipes(registration));

			registration.addRecipes(RecipeTypes.CRAFTING, ToolboxColoringRecipeMaker.createRecipes().toList());
		}

		@Override
		public void registerRecipeCatalysts(@NotNull IRecipeCatalystRegistration registration) {
			allCategories.forEach(c -> c.registerCatalysts(registration));
		}

		@Override
		public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration) {
			registration.addRecipeTransferHandler(new BlueprintTransferHandler(), RecipeTypes.CRAFTING);
		}

		@Override
		public <T> void registerFluidSubtypes(ISubtypeRegistration registration, @NotNull IPlatformFluidHelper<T> platformFluidHelper) {
			PotionFluidSubtypeInterpreter interpreter = new PotionFluidSubtypeInterpreter();
			PotionFluid potionFluid = AllFluids.POTION.get();
			registration.registerSubtypeInterpreter(ForgeTypes.FLUID_STACK, potionFluid.getSource(), interpreter);
			registration.registerSubtypeInterpreter(ForgeTypes.FLUID_STACK, potionFluid.getFlowing(), interpreter);
		}

		@Override
		public void registerGuiHandlers(IGuiHandlerRegistration registration) {
			registration.addGenericGuiContainerHandler(AbstractSimiContainerScreen.class, new SlotMover());

			registration.addGhostIngredientHandler(AbstractFilterScreen.class, new GhostIngredientHandler());
			registration.addGhostIngredientHandler(BlueprintScreen.class, new GhostIngredientHandler());
			registration.addGhostIngredientHandler(LinkedControllerScreen.class, new GhostIngredientHandler());
			registration.addGhostIngredientHandler(ScheduleScreen.class, new GhostIngredientHandler());
		}

		private class CategoryBuilder<T extends Recipe<?>> {
			private final Class<? extends T> recipeClass;
			private final Predicate<CRecipes> predicate = cRecipes -> true;

			private IDrawable background;
			private IDrawable icon;

			private final List<Consumer<List<T>>> recipeListConsumers = new ArrayList<>();
			private final List<Supplier<? extends ItemStack>> catalysts = new ArrayList<>();

			public CategoryBuilder(Class<? extends T> recipeClass) {
				this.recipeClass = recipeClass;
			}

			public CmiFanJeiPlugin.CategoryBuilder<T> addRecipeListConsumer(Consumer<List<T>> consumer) {
				recipeListConsumers.add(consumer);
				return this;
			}

			public CmiFanJeiPlugin.CategoryBuilder<T> addTypedRecipes(IRecipeTypeInfo recipeTypeEntry) {
				return addTypedRecipes(recipeTypeEntry::getType);
			}

			public CmiFanJeiPlugin.CategoryBuilder<T> addTypedRecipes(Supplier<RecipeType<? extends T>> recipeType) {
				return addRecipeListConsumer((recipes) -> {
					CreateJEI.<T>consumeTypedRecipes(recipes::add, recipeType.get());
				});
			}

			public CmiFanJeiPlugin.CategoryBuilder<T> catalystStack(Supplier<ItemStack> supplier) {
				catalysts.add(supplier);
				return this;
			}

			public CmiFanJeiPlugin.CategoryBuilder<T> icon(IDrawable icon) {
				this.icon = icon;
				return this;
			}

			public CmiFanJeiPlugin.CategoryBuilder<T> doubleItemIcon(ItemLike item1, ItemLike item2) {
				icon(new DoubleItemIcon(() -> new ItemStack(item1), () -> new ItemStack(item2)));
				return this;
			}

			public CmiFanJeiPlugin.CategoryBuilder<T> background(IDrawable background) {
				this.background = background;
				return this;
			}

			public CmiFanJeiPlugin.CategoryBuilder<T> emptyBackground(int width, int height) {
				background(new EmptyBackground(width, height));
				return this;
			}

			public CreateRecipeCategory<T> build(String name, CreateRecipeCategory.Factory<T> factory) {
				Supplier<List<T>> recipesSupplier;
				if (predicate.test(AllConfigs.server().recipes)) {
					recipesSupplier = () -> {
						List<T> recipes = new ArrayList<>();
						for (Consumer<List<T>> consumer : recipeListConsumers) {
							consumer.accept(recipes);
						}
						return recipes;
					};
				} else {
					recipesSupplier = () -> Collections.emptyList();
				}

				CreateRecipeCategory.Info<T> info = new CreateRecipeCategory.Info<>(
						new mezz.jei.api.recipe.RecipeType<>(Create.asResource(name), recipeClass),
						Lang.translateDirect("recipe." + name), background, icon, recipesSupplier, catalysts
				);
				CreateRecipeCategory<T> category = factory.create(info);
				allCategories.add(category);
				return category;
			}
		}
	}
}