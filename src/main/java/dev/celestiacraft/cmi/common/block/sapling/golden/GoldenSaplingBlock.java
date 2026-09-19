package dev.celestiacraft.cmi.common.block.sapling.golden;

import dev.celestiacraft.cmi.datagen.worldgen.tree.GoldenTreeGrower;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;

public class GoldenSaplingBlock extends SaplingBlock {
	public GoldenSaplingBlock(Properties properties) {
		super(new GoldenTreeGrower(), properties
				.mapColor(MapColor.PLANT)
				.noCollission()
				.randomTicks()
				.instabreak()
				.sound(SoundType.GRASS)
				.pushReaction(PushReaction.DESTROY));
	}
}