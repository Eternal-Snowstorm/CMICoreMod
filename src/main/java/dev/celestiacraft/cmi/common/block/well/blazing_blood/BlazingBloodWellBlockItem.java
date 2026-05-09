package dev.celestiacraft.cmi.common.block.well.blazing_blood;

import dev.celestiacraft.libs.api.client.context.TooltipContext;
import dev.celestiacraft.libs.api.register.multiblock.ControllerBlock;
import dev.celestiacraft.libs.api.register.multiblock.ControllerBlockItem;

public class BlazingBloodWellBlockItem extends ControllerBlockItem {
	public BlazingBloodWellBlockItem(ControllerBlock block, Properties properties) {
		super(block, properties);
	}

	@Override
	public void addTooltips(TooltipContext context) {
		context.addTranslatable("cmi.tooltip.blazing_blood_well.runningEnvironment");
		super.addTooltips(context);
	}
}