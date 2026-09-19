package dev.celestiacraft.cmi.datagen.worldgen;

import dev.celestiacraft.cmi.Cmi;
import dev.celestiacraft.cmi.datagen.worldgen.tree.GlacianFoliagePlacer;
import dev.celestiacraft.cmi.datagen.worldgen.tree.GlacianTrunkPlacer;
import dev.celestiacraft.cmi.datagen.worldgen.tree.SnowCapDecorator;
import earth.terrarium.adastra.common.registry.ModBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.featuresize.TwoLayersFeatureSize;
import net.minecraft.world.level.levelgen.feature.foliageplacers.BlobFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.trunkplacers.StraightTrunkPlacer;

import java.util.List;

public class CmiConfiguredFeatures {
	public static ResourceKey<ConfiguredFeature<?, ?>>
			GOLDEN_TREE,
			GLACIAN_TREE;

	static {
		GOLDEN_TREE = registerKey("golden_tree");
		GLACIAN_TREE = registerKey("glacian_tree");
	}

	private static ResourceKey<ConfiguredFeature<?, ?>> registerKey(ResourceLocation name) {
		return ResourceKey.create(Registries.CONFIGURED_FEATURE, name);
	}

	private static ResourceKey<ConfiguredFeature<?, ?>> registerKey(String name) {
		return registerKey(Cmi.loadResource(name));
	}

	private static TreeConfiguration.TreeConfigurationBuilder createStraightBlobTree(Block logBlock, Block leavesBlock, int baseHeight, int heightRandA, int heightRandB, int radius) {
		return new TreeConfiguration.TreeConfigurationBuilder(
				BlockStateProvider.simple(logBlock),
				new StraightTrunkPlacer(
						baseHeight,
						heightRandA,
						heightRandB
				),
				BlockStateProvider.simple(leavesBlock),
				new BlobFoliagePlacer(
						ConstantInt.of(radius),
						ConstantInt.of(0),
						3
				),
				new TwoLayersFeatureSize(
						1,
						0,
						1
				)
		);
	}

	private static TreeConfiguration.TreeConfigurationBuilder createGold() {
		return createStraightBlobTree(
				Blocks.GOLD_BLOCK,
				Blocks.GOLD_ORE,
				4,
				2,
				0,
				2
		).ignoreVines();
	}

	private static TreeConfiguration.TreeConfigurationBuilder createGlacian() {
		return new TreeConfiguration.TreeConfigurationBuilder(
				BlockStateProvider.simple(ModBlocks.GLACIAN_LOG.get()),
				new GlacianTrunkPlacer(
						14,
						3,
						4,
						2,
						2,
						3,
						4,
						4,
						UniformInt.of(3, 5),
						1,
						3,
						3,
						3,
						0.5F
				),
				BlockStateProvider.simple(ModBlocks.GLACIAN_LEAVES.get()),
				new GlacianFoliagePlacer(
						ConstantInt.of(3),
						ConstantInt.of(0),
						3,
						0.1F,
						0.5F
				),
				new TwoLayersFeatureSize(
						1,
						0,
						3
				))
				.dirt(BlockStateProvider.simple(ModBlocks.PERMAFROST.get()))
				.decorators(List.of(
						new SnowCapDecorator(BlockStateProvider.simple(Blocks.SNOW), 0.5F),
						new SnowCapDecorator(BlockStateProvider.simple(Blocks.SNOW_BLOCK), 0.1F)
				))
				.ignoreVines();
	}

	private static <FC extends FeatureConfiguration, F extends Feature<FC>> void register(
			BootstapContext<ConfiguredFeature<?, ?>> context,
			ResourceKey<ConfiguredFeature<?, ?>> key,
			F feature,
			FC configuration
	) {
		context.register(key, new ConfiguredFeature<>(feature, configuration));
	}

	public static void bootstrap(BootstapContext<ConfiguredFeature<?, ?>> context) {
		register(context, GOLDEN_TREE, Feature.TREE, createGold().build());
		register(context, GLACIAN_TREE, Feature.TREE, createGlacian().build());
	}
}