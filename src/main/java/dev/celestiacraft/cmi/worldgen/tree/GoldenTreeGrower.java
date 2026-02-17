package dev.celestiacraft.cmi.worldgen.tree;

import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.block.grower.AbstractTreeGrower;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import dev.celestiacraft.cmi.worldgen.ModConfiguredFeatures;

public class GoldenTreeGrower extends AbstractTreeGrower {
	public GoldenTreeGrower() {
		super();
	}

	@Override
	protected @Nullable ResourceKey<ConfiguredFeature<?, ?>> getConfiguredFeature(@NotNull RandomSource random, boolean hasBees) {
		return ModConfiguredFeatures.GOLDEN_TREE;
	}
}