package dev.celestiacraft.cmi.event;

import com.simibubi.create.AllItems;
import dev.celestiacraft.cmi.api.register.block.MetalCogWheelRegister;
import dev.celestiacraft.cmi.common.register.CmiCreativeTab;
import dev.celestiacraft.cmi.common.register.CmiItem;
import dev.celestiacraft.cmi.common.register.block.*;
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
		ResourceKey<CreativeModeTab> key = event.getTabKey();

		if (key.equals(KUBEJS_TAB)) {
			List.of(
					VentBlocks.MARS_GEO,
					VentBlocks.MERCURY_GEO,
					WallBlocks.WATER_WELL,
					WallBlocks.LAVA_WELL,
					WallBlocks.BLAZING_BLOOD_WELL,
					MachineBlocks.ACCELERATOR,
					MachineBlocks.ACCELERATOR_MOTOR,
					MachineBlocks.STEAM_HAMMER,
					MachineBlocks.ADVANCED_SPOUT,
					MachineBlocks.VOID_DUST_COLLECTOR,
					MachineBlocks.BELT_GRINDER,
					MachineBlocks.GEOTHERMAL_GENERATOR,
					FluidBurnerBlocks.BRONZE_FLUID_BURNER,
					FluidBurnerBlocks.CAST_IRON_FLUID_BURNER,
					FluidBurnerBlocks.STEEL_FLUID_BURNER,
					SolarBoilerBlocks.BRONZE_SOLAR_BOILER,
					SolarBoilerBlocks.CAST_IRON_SOLAR_BOILER,
					SolarBoilerBlocks.STEEL_SOLAR_BOILER,
					OtherBlocks.WIND_VANE
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

		if (key.equals(CmiCreativeTab.MECHANISMS)) {
			event.accept(AllItems.PRECISION_MECHANISM.get());
			event.accept(REDSTONE_MECHANISM);
		}
	}
}