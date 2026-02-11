package top.nebula.cmi.event;

import com.tterrag.registrate.util.entry.BlockEntry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.nebula.cmi.common.register.CmiBlocks;

import java.util.List;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class AddCreativeModeTabs {
	public static final ResourceKey<CreativeModeTab> KUBEJS_TAB = ResourceKey.create(
			Registries.CREATIVE_MODE_TAB,
			ResourceLocation.parse("kubejs:tab")
	);

	@SubscribeEvent
	public static void buildContents(BuildCreativeModeTabContentsEvent event) {
		if (event.getTabKey() == KUBEJS_TAB) {
			List<? extends BlockEntry<? extends Block>> list = List.of(
					CmiBlocks.MARS_GEO,
					CmiBlocks.MERCURY_GEO,
					CmiBlocks.WATER_PUMP,
					CmiBlocks.ACCELERATOR_MOTOR,
					CmiBlocks.STEAM_HAMMER,
					CmiBlocks.ADVANCED_SPOUT,
					CmiBlocks.VOID_DUST_COLLECTOR,
					CmiBlocks.BELT_GRINDER
			);
			list.forEach((block) -> {
				event.accept(block.asItem());
			});
		}
	}
}