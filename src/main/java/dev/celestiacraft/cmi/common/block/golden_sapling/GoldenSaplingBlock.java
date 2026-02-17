package dev.celestiacraft.cmi.common.block.golden_sapling;

import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import dev.celestiacraft.cmi.worldgen.tree.GoldenTreeGrower;

public class GoldenSaplingBlock extends SaplingBlock {
	public GoldenSaplingBlock(BlockBehaviour.Properties properties) {
		super(new GoldenTreeGrower(), properties);
	}
}