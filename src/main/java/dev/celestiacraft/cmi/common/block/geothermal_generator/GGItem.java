package dev.celestiacraft.cmi.common.block.geothermal_generator;

import dev.celestiacraft.libs.api.client.context.TooltipContext;
import dev.celestiacraft.libs.api.register.block.BasicBlockItem;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Block;

import java.util.List;

public class GGItem extends BasicBlockItem {
	public GGItem(Block block, Properties properties) {
		super(block, properties);
	}

	@Override
	public void addTooltips(TooltipContext context) {
		List<Component> tooltip = context.getTooltip();
	}
}