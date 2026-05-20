package dev.celestiacraft.cmi.common.register;

import com.tterrag.registrate.providers.RegistrateRecipeProvider;
import com.tterrag.registrate.util.entry.ItemEntry;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;
import dev.celestiacraft.cmi.Cmi;
import dev.celestiacraft.cmi.api.client.assets.ItemModelGen;
import dev.celestiacraft.cmi.common.item.*;
import dev.celestiacraft.cmi.common.item.tool.crafting_table.HandheleCraftingTableItem;
import dev.celestiacraft.cmi.tags.CmiItemTags;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ForgeSpawnEggItem;

public class CmiItem {
	public static final ItemEntry<TestBrushItem> TEST_BRUSH;
	public static final ItemEntry<MysticPomeloItem> MYSTIC_POMELO;
	public static final ItemEntry<SimpleBatteryItem> SIMPLE_BATTERY;
	public static final ItemEntry<ForgeSpawnEggItem> QI_MONTH_EGG;
	public static final ItemEntry<InitialItemKitItem> INITIAL_ITEM_KIT;
	public static final ItemEntry<HandheleCraftingTableItem> HANDHELE_CRAFTING_TABLE;
	public static final ItemEntry<NutritionSyringeItem> NUTRITION_SYRINGE;

	static {
		TEST_BRUSH = Cmi.REGISTRATE.item("test_brush", TestBrushItem::new)
				.register();
		NUTRITION_SYRINGE = Cmi.REGISTRATE.item("nutrition_syringe", NutritionSyringeItem::new)
				.model(NonNullBiConsumer.noop())
				.register();
		MYSTIC_POMELO = Cmi.REGISTRATE.item("mystic_pomelo", MysticPomeloItem::new)
				.model(ItemModelGen.generated("item/mystic_pomelo"))
				.register();
		SIMPLE_BATTERY = Cmi.REGISTRATE.item("simple_battery", SimpleBatteryItem::new)
				.model(ItemModelGen.generated("item/simple_battery"))
				.register();
		INITIAL_ITEM_KIT = Cmi.REGISTRATE.item("initial_item_kit", InitialItemKitItem::new)
				.model(NonNullBiConsumer.noop())
				.register();
		HANDHELE_CRAFTING_TABLE = Cmi.REGISTRATE.item("handheld_crafting_table", HandheleCraftingTableItem::new)
				.model(ItemModelGen.handheld("item/tool/handheld_crafting_table"))
				.recipe((context, provider) -> {
					ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, context.get())
							.requires(CmiItemTags.WORKBENCHS)
							.unlockedBy("crafting_table", RegistrateRecipeProvider.has(CmiItemTags.WORKBENCHS))
							.save(provider, Cmi.loadResource("minecraft/crafting/shapeless/" + context.getName()));
				})
				.register();
		QI_MONTH_EGG = Cmi.REGISTRATE.item("qi_month_spawn_egg", (properties) -> {
					return new ForgeSpawnEggItem(
							CmiEntity.QI_MONTH,
							0x2E2E2E,
							0x8A8A8A,
							properties
					);
				})
				.model(ItemModelGen.withModel(ResourceLocation.withDefaultNamespace("item/template_spawn_egg")))
				.register();
	}

	public static void register() {
		Cmi.LOGGER.info("CMI Core Items Registered!");
	}
}