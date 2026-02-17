package dev.celestiacraft.cmi.compat.jade;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;
import dev.celestiacraft.cmi.common.block.advanced_spout.AdvancedSpoutBlock;

public enum CmiComponentProvider implements IBlockComponentProvider {
	INSTANCE;

	/**
	 * @param tooltip
	 * @param block
	 * @param config
	 */
	@Override
	public void appendTooltip(ITooltip tooltip, BlockAccessor block, IPluginConfig config) {
		BlockState state = block.getBlockState();

		if (state.getValue(AdvancedSpoutBlock.POWERED)) {
			tooltip.add(Component.translatable("jade.tip.cmi.powered.on"));
		} else {
			tooltip.add(Component.translatable("jade.tip.cmi.powered.off"));
		}
	}

	@Override
	public ResourceLocation getUid() {
		return CmiType.COMMON;
	}
}