package dev.celestiacraft.cmi.common.block.well.water;

import dev.celestiacraft.libs.api.client.context.TooltipContext;
import dev.celestiacraft.libs.api.register.multiblock.ControllerBlock;
import dev.celestiacraft.libs.api.register.multiblock.ControllerBlockItem;

public class WaterWellBlockItem extends ControllerBlockItem {
	public WaterWellBlockItem(ControllerBlock block, Properties properties) {
		super(block, properties);
	}

	@Override
	public void addTooltips(TooltipContext context) {
		context.addTranslatable("cmi.tooltip.water_well.runningEnvironment");
		super.addTooltips(context);
	}
}