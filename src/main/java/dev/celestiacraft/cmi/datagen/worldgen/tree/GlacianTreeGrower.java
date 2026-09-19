package dev.celestiacraft.cmi.datagen.worldgen.tree;

import dev.celestiacraft.cmi.datagen.worldgen.CmiConfiguredFeatures;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.grower.AbstractTreeGrower;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GlacianTreeGrower extends AbstractTreeGrower {
	@Override
	protected @Nullable ResourceKey<ConfiguredFeature<?, ?>> getConfiguredFeature(@NotNull RandomSource random, boolean hasBees) {
		return CmiConfiguredFeatures.GLACIAN_TREE;
	}
}