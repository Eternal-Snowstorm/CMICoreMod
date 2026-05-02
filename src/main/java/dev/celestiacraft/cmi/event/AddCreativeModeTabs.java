package dev.celestiacraft.cmi.event;

import com.simibubi.create.AllItems;
import dev.celestiacraft.cmi.common.register.CmiBlock;
import dev.celestiacraft.cmi.common.register.CmiCreativeTab;
import dev.celestiacraft.cmi.common.register.CmiItem;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class AddCreativeModeTabs {
	private static final ResourceKey<CreativeModeTab> KUBEJS_TAB = ResourceKey.create(
			Registries.CREATIVE_MODE_TAB,
			ResourceLocation.parse("kubejs:tab")
	);

	private static final Item REDSTONE_MECHANISM = ForgeRegistries.ITEMS.getValue(
			ResourceLocation.fromNamespaceAndPath(
					"vintageimprovements",
					"redstone_module"
			)
	);

	@SubscribeEvent
	public static void buildContents(BuildCreativeModeTabContentsEvent event) {
		if (event.getTabKey() == KUBEJS_TAB) {
			List.of(
					CmiBlock.MARS_GEO,
					CmiBlock.MERCURY_GEO,
					CmiBlock.WATER_WELL,
					CmiBlock.LAVA_WELL,
					CmiBlock.BLAZING_BLOOD_WELL,
					CmiBlock.ACCELERATOR,
					CmiBlock.ACCELERATOR_MOTOR,
					CmiBlock.STEAM_HAMMER,
					CmiBlock.ADVANCED_SPOUT,
					CmiBlock.VOID_DUST_COLLECTOR,
					CmiBlock.BELT_GRINDER,
					CmiBlock.BRONZE_FLUID_BURNER,
					CmiBlock.BRONZE_SOLAR_BOILER,
					CmiBlock.CAST_IRON_FLUID_BURNER,
					CmiBlock.CAST_IRON_SOLAR_BOILER,
					CmiBlock.STEEL_FLUID_BURNER,
					CmiBlock.STEEL_SOLAR_BOILER
			).forEach((block) -> {
				event.accept(block.asItem());
			});

			List.of(
					CmiItem.INITIAL_ITEM_KIT
			).forEach((item) -> {
				event.accept(item.asItem());
			});
		}

		if (event.getTabKey() == CmiCreativeTab.MECHANISMS) {
			event.accept(AllItems.PRECISION_MECHANISM.get());
			event.accept(REDSTONE_MECHANISM);
		}
	}
}