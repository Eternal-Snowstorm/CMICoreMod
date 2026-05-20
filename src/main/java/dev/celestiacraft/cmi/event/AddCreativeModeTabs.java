package dev.celestiacraft.cmi.event;

import com.simibubi.create.AllItems;
import dev.celestiacraft.cmi.api.register.block.MetalCogWheelRegister;
import dev.celestiacraft.cmi.common.register.CmiBlock;
import dev.celestiacraft.cmi.common.register.CmiCreativeTab;
import dev.celestiacraft.cmi.common.register.CmiItem;
import dev.celestiacraft.cmi.utils.ModResources;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class AddCreativeModeTabs {
	private static final ResourceKey<CreativeModeTab> KUBEJS_TAB = ResourceKey.create(
			Registries.CREATIVE_MODE_TAB,
			ModResources.loadKubeJS("tab").getLocation()
	);

	private static final Item REDSTONE_MECHANISM = ModResources.RESSTONE_MODULE.getItem();

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
					CmiBlock.STEEL_SOLAR_BOILER,
					CmiBlock.WIND_VANE
			).forEach((block) -> {
				event.accept(block.asItem());
			});

			List.of(
					CmiItem.INITIAL_ITEM_KIT,
					CmiItem.HANDHELE_CRAFTING_TABLE
			).forEach((item) -> {
				event.accept(item.asItem());
			});

			MetalCogWheelRegister.COMMON_LIST.forEach((cogwheel) -> {
				event.accept(cogwheel.asItem());
			});
		}

		if (event.getTabKey().equals(CmiCreativeTab.MECHANISMS)) {
			event.accept(AllItems.PRECISION_MECHANISM.get());
			event.accept(REDSTONE_MECHANISM);
		}
	}
}