package dev.celestiacraft.cmi.common.block.advanced_spout;

import dev.celestiacraft.cmi.api.register.block.BasicCreateBlockItem;
import dev.celestiacraft.libs.api.client.context.TooltipContext;
import dev.celestiacraft.libs.api.register.block.BasicBlock;

public class AdvancedSpoutItem extends BasicCreateBlockItem {
	public AdvancedSpoutItem(BasicBlock block, Properties properties) {
		super(block, properties);
	}

	@Override
	public void addTooltips(TooltipContext context) {
		super.addTooltips(context);
	}
}