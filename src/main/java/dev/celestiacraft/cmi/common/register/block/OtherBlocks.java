package dev.celestiacraft.cmi.common.register.block;

import blusunrize.immersiveengineering.common.blocks.generic.ScaffoldingBlock;
import com.simibubi.create.AllTags;
import com.simibubi.create.foundation.data.SharedProperties;
import com.simibubi.create.foundation.data.TagGen;
import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;
import dev.celestiacraft.cmi.Cmi;
import dev.celestiacraft.cmi.api.client.assets.ItemModelGen;
import dev.celestiacraft.cmi.common.block.golden_sapling.GoldenSaplingBlock;
import dev.celestiacraft.cmi.common.block.test_gravel.TestGravelBlock;
import dev.celestiacraft.cmi.common.block.wind_vane.WindVaneBlock;
import dev.celestiacraft.cmi.common.block.wind_vane.WindVaneBlockItem;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.client.model.generators.BlockModelProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.common.Tags;

public class OtherBlocks {
	public static final BlockEntry<TestGravelBlock> TEST_GRAVEL;
	public static final BlockEntry<GoldenSaplingBlock> GOLD_SAPLING;
	public static final BlockEntry<ScaffoldingBlock> NAHUATL_SCAFFOLD;
	public static final BlockEntry<ScaffoldingBlock> BLAZEWOOD_SCAFFOLD;
	public static final BlockEntry<WindVaneBlock> WIND_VANE;

	static {
		NAHUATL_SCAFFOLD = Cmi.REGISTRATE.block("nahuatl_scaffold", ScaffoldingBlock::new)
				.properties(BlockBehaviour.Properties::noOcclusion)
				.initialProperties(SharedProperties::wooden)
				.transform(TagGen.axeOnly())
				.tag(BlockTags.MINEABLE_WITH_AXE)
				.tag(Tags.Blocks.NEEDS_WOOD_TOOL)
				.blockstate((context, provider) -> {
					provider.getVariantBuilder(context.get())
							.forAllStates((state) -> {
								BlockModelProvider models = provider.models();
								return ConfiguredModel.builder()
										.modelFile(models.getExistingFile(provider.modLoc("block/scaffold/nahuatl")))
										.build();
							});
				})
				.item()
				.model(ItemModelGen.withModel("block/scaffold/nahuatl"))
				.build()
				.register();

		BLAZEWOOD_SCAFFOLD = Cmi.REGISTRATE.block("blazewood_scaffold", ScaffoldingBlock::new)
				.properties(BlockBehaviour.Properties::noOcclusion)
				.initialProperties(SharedProperties::wooden)
				.transform(TagGen.axeOnly())
				.tag(BlockTags.MINEABLE_WITH_AXE)
				.tag(Tags.Blocks.NEEDS_WOOD_TOOL)
				.blockstate((context, provider) -> {
					provider.getVariantBuilder(context.get())
							.forAllStates((state) -> {
								BlockModelProvider models = provider.models();
								return ConfiguredModel.builder()
										.modelFile(models.getExistingFile(provider.modLoc("block/scaffold/blazewood")))
										.build();
							});
				})
				.item()
				.model(ItemModelGen.withModel("block/scaffold/blazewood"))
				.build()
				.register();

		WIND_VANE = Cmi.REGISTRATE.block("wind_vane", WindVaneBlock::new)
				.initialProperties(SharedProperties::copperMetal)
				.item(WindVaneBlockItem::new)
				.model(NonNullBiConsumer.noop())
				.build()
				.tag(BlockTags.MINEABLE_WITH_PICKAXE)
				.tag(BlockTags.NEEDS_STONE_TOOL)
				.tag(AllTags.AllBlockTags.WRENCH_PICKUP.tag)
				.blockstate(NonNullBiConsumer.noop())
				.register();

		TEST_GRAVEL = Cmi.REGISTRATE.block("test_gravel", TestGravelBlock::new)
				.item()
				.build()
				.register();

		GOLD_SAPLING = Cmi.REGISTRATE.block("gold_sapling", GoldenSaplingBlock::new)
				.blockstate(NonNullBiConsumer.noop())
				.item()
				.build()
				.register();
	}

	public static void register() {
		Cmi.LOGGER.info("{} Other Blocks Registered!", Cmi.MODID);
	}
}