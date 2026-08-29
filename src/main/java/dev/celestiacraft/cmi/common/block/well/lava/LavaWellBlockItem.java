package dev.celestiacraft.cmi.common.block.well.lava;

import dev.celestiacraft.libs.api.client.context.TooltipContext;
import dev.celestiacraft.libs.api.register.multiblock.ControllerBlock;
import dev.celestiacraft.libs.api.register.multiblock.ControllerBlockItem;

public class LavaWellBlockItem extends ControllerBlockItem {
	public LavaWellBlockItem(ControllerBlock block, Properties properties) {
		super(block, properties);
	}

	@Override
	public void addTooltips(TooltipContext context) {
		context.addTranslatable("cmi.tooltip.lava_well.runningEnvironment");
		super.addTooltips(context);
	}
}