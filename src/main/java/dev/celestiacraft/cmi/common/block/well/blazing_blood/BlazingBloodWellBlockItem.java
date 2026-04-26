package dev.celestiacraft.cmi.common.block.well.blazing_blood;

import dev.celestiacraft.libs.api.client.tooltip.TooltipContext;
import dev.celestiacraft.libs.api.register.multiblock.ControllerBlock;
import dev.celestiacraft.libs.api.register.multiblock.ControllerBlockItem;
import net.minecraft.network.chat.Component;

import java.util.List;

public class BlazingBloodWellBlockItem extends ControllerBlockItem {
	public BlazingBloodWellBlockItem(ControllerBlock block, Properties properties) {
		super(block, properties);
	}

	@Override
	public void addTooltips(TooltipContext context) {
		List<Component> tooltip = context.getTooltip();
		tooltip.add(Component.translatable("cmi.tooltip.blazing_blood_well.runningEnvironment"));
		super.addTooltips(context);
	}
}