package top.nebula.cmi.event;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.nebula.cmi.common.register.ModBlocks;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class AddCreativeModeTabs {
	public static final ResourceKey<CreativeModeTab> KUBEJS_TAB = ResourceKey.create(
			Registries.CREATIVE_MODE_TAB,
			ResourceLocation.parse("kubejs:tab")
	);

	@SubscribeEvent
	public static void buildContents(BuildCreativeModeTabContentsEvent event) {
		if (event.getTabKey() == KUBEJS_TAB) {
			event.accept(ModBlocks.ACCELERATOR_MOTOR.asItem());
			event.accept(ModBlocks.STEAM_HAMMER.asItem());
			event.accept(ModBlocks.MARS_GEO.asItem());
			event.accept(ModBlocks.MERCURY_GEO.asItem());
			event.accept(ModBlocks.ADVANCED_SPOUT.asItem());
			event.accept(ModBlocks.VOID_DUST_COLLECTOR.asItem());
		}
	}
}