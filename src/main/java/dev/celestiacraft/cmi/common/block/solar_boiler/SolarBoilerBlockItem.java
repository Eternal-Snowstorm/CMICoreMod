package dev.celestiacraft.cmi.common.block.solar_boiler;

import dev.celestiacraft.libs.api.client.tooltip.TooltipContext;
import dev.celestiacraft.libs.api.register.block.BasicBlockItem;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Block;

import java.util.List;

public class SolarBoilerBlockItem extends BasicBlockItem {
	public SolarBoilerBlockItem(Block block, Properties properties) {
		super(block, properties);
	}

	@Override
	public void addTooltips(TooltipContext context) {
		List<Component> tooltip = context.getTooltip();
	}
}