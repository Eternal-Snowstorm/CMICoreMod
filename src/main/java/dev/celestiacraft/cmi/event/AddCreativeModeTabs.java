package dev.celestiacraft.cmi.event;

import com.simibubi.create.AllItems;
import com.tterrag.registrate.util.entry.BlockEntry;
import dev.celestiacraft.cmi.common.register.CmiCreativeTab;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import dev.celestiacraft.cmi.common.register.CmiBlocks;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class AddCreativeModeTabs {
	private static final ResourceKey<CreativeModeTab> KUBEJS_TAB = ResourceKey.create(
			Registries.CREATIVE_MODE_TAB,
			ResourceLocation.parse("kubejs:tab")
	);

	private static final Lazy<Item> REDSTONE_MECHANISM = Lazy.of(() -> {
		return ForgeRegistries.ITEMS.getValue(ResourceLocation.fromNamespaceAndPath(
				"vintageimprovements",
				"redstone_module"
		));
	});

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

		if (event.getTabKey() == CmiCreativeTab.MECHANISMS) {
			event.accept(AllItems.PRECISION_MECHANISM.get());
			event.accept(REDSTONE_MECHANISM.get());
		}
	}
}