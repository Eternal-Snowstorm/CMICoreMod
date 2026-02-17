package dev.celestiacraft.cmi.worldgen;

import dev.celestiacraft.cmi.Cmi;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.valueproviders.ConstantInt;
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

public class ModConfiguredFeatures {
	public static ResourceKey<ConfiguredFeature<?, ?>> GOLDEN_TREE = registerKey("golden_tree");

	private static ResourceKey<ConfiguredFeature<?, ?>> registerKey(String name) {
		return ResourceKey.create(Registries.CONFIGURED_FEATURE, Cmi.loadResource(name));
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
		return createStraightBlobTree(Blocks.GOLD_BLOCK, Blocks.GOLD_ORE, 4, 2, 0, 2).ignoreVines();
	}

	private static <FC extends FeatureConfiguration, F extends Feature<FC>> void register(BootstapContext<ConfiguredFeature<?, ?>> context, ResourceKey<ConfiguredFeature<?, ?>> key, F feature, FC featureConfiguration) {
		context.register(key, new ConfiguredFeature<>(feature, featureConfiguration));
	}

	public static void bootstrap(BootstapContext<ConfiguredFeature<?, ?>> context) {
		register(context, GOLDEN_TREE, Feature.TREE, createGold().build());
	}
}