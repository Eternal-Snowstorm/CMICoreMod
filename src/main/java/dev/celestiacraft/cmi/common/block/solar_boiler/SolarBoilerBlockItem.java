package dev.celestiacraft.cmi.common.block.solar_boiler;

import dev.celestiacraft.cmi.common.register.CmiBlock;
import dev.celestiacraft.cmi.config.common.SolarBoilerConfig;
import dev.celestiacraft.libs.api.client.context.TooltipContext;
import dev.celestiacraft.libs.api.register.block.BasicBlockItem;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Block;

public class SolarBoilerBlockItem extends BasicBlockItem {
	public SolarBoilerBlockItem(Block block, Properties properties) {
		super(block, properties);
	}

	@Override
	public void addTooltips(TooltipContext context) {
		int efficiency = 0;
		int capacity = 0;

		if (getBlock() == CmiBlock.BRONZE_SOLAR_BOILER.get()) {
			efficiency = SolarBoilerConfig.BRONZE_EFFICIENCY.get();
			capacity = SolarBoilerConfig.BRONZE_CAPACITY.get();
		} else if (getBlock() == CmiBlock.CAST_IRON_SOLAR_BOILER.get()) {
			efficiency = SolarBoilerConfig.CAST_IRON_EFFICIENCY.get();
			capacity = SolarBoilerConfig.CAST_IRON_CAPACITY.get();
		} else if (getBlock() == CmiBlock.STEEL_SOLAR_BOILER.get()) {
			efficiency = SolarBoilerConfig.STEEL_EFFICIENCY.get();
			capacity = SolarBoilerConfig.STEEL_CAPACITY.get();
		}

		context.add(Component.translatable(
				"cmi.tooltip.solar_boiler.efficiency",
				efficiency
		).withStyle(ChatFormatting.GRAY));

		context.add(Component.translatable(
				"cmi.tooltip.solar_boiler.capacity",
				capacity
		).withStyle(ChatFormatting.GRAY));

		context.add(Component.translatable(
				"cmi.tooltip.solar_boiler.total_capacity",
				capacity * 2
		).withStyle(ChatFormatting.GRAY));
	}
}