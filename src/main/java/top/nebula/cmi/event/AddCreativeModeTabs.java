package top.nebula.cmi.event;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.nebula.cmi.common.register.CmiBlocks;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class AddCreativeModeTabs {
	public static final ResourceKey<CreativeModeTab> KUBEJS_TAB = ResourceKey.create(
			Registries.CREATIVE_MODE_TAB,
			ResourceLocation.parse("kubejs:tab")
	);

	@SubscribeEvent
	public static void buildContents(BuildCreativeModeTabContentsEvent event) {
		if (event.getTabKey() == KUBEJS_TAB) {
			event.accept(CmiBlocks.MARS_GEO.asItem());
			event.accept(CmiBlocks.MERCURY_GEO.asItem());
			event.accept(CmiBlocks.WATER_PUMP.asItem());
			event.accept(CmiBlocks.ACCELERATOR_MOTOR.asItem());
			event.accept(CmiBlocks.STEAM_HAMMER.asItem());
			event.accept(CmiBlocks.ADVANCED_SPOUT.asItem());
			event.accept(CmiBlocks.VOID_DUST_COLLECTOR.asItem());
			event.accept(CmiBlocks.BELT_GRINDER.asItem());
		}
	}
}