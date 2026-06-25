package dev.celestiacraft.cmi.common.block.solar_boiler;

import com.simibubi.create.foundation.item.TooltipHelper;
import dev.celestiacraft.cmi.api.client.CmiLang;
import dev.celestiacraft.cmi.common.register.block.SolarBoilerBlocks;
import dev.celestiacraft.cmi.config.common.SolarBoilerConfig;
import dev.celestiacraft.libs.api.client.context.TooltipContext;
import dev.celestiacraft.libs.api.register.block.BasicBlockItem;
import net.createmod.catnip.lang.FontHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Block;

import java.util.List;

public class SolarBoilerItem extends BasicBlockItem {
	public SolarBoilerItem(Block block, Properties properties) {
		super(block, properties);
	}

	private int efficiency = 0;
	private int capacity = 0;

	private void judgmentBlock() {
		if (getBlock() == SolarBoilerBlocks.BRONZE_SOLAR_BOILER.get()) {
			efficiency = SolarBoilerConfig.BRONZE_EFFICIENCY.get();
			capacity = SolarBoilerConfig.BRONZE_CAPACITY.get();
		} else if (getBlock() == SolarBoilerBlocks.CAST_IRON_SOLAR_BOILER.get()) {
			efficiency = SolarBoilerConfig.CAST_IRON_EFFICIENCY.get();
			capacity = SolarBoilerConfig.CAST_IRON_CAPACITY.get();
		} else if (getBlock() == SolarBoilerBlocks.STEEL_SOLAR_BOILER.get()) {
			efficiency = SolarBoilerConfig.STEEL_EFFICIENCY.get();
			capacity = SolarBoilerConfig.STEEL_CAPACITY.get();
		}
	}

	@Override
	public void addTooltips(TooltipContext context) {
		judgmentBlock();
		addSummaryTooltip(context);
		addShiftTooltip(context);
		addCtrlTooltip(context);
	}

	private void addSummaryTooltip(TooltipContext context) {
		List<Component> tooltip = context.getTooltip();

		tooltip.addAll(TooltipHelper.cutStringTextComponent(
				CmiLang.translateDirect("tooltip.solar_boiler.summary").getString(),
				FontHelper.Palette.STANDARD_CREATE
		));
	}

	private void addShiftTooltip(TooltipContext context) {
		List<Component> tooltip = context.getTooltip();

		CmiLang.isShiftDown(tooltip);
		if (context.isShiftDown()) {
			context.addEmpty();

			tooltip.addAll(TooltipHelper.cutStringTextComponent(
					CmiLang.translateDirect("tooltip.solar_boiler.workCondition").getString(),
					FontHelper.Palette.STANDARD_CREATE.primary(),
					FontHelper.Palette.STANDARD_CREATE.highlight()
			));

			tooltip.addAll(TooltipHelper.cutStringTextComponent(
					CmiLang.translateDirect("tooltip.solar_boiler.condition.1").getString(),
					FontHelper.Palette.STANDARD_CREATE.primary(),
					FontHelper.Palette.STANDARD_CREATE.highlight()
			));
			tooltip.addAll(TooltipHelper.cutStringTextComponent(
					CmiLang.translateDirect("tooltip.solar_boiler.condition.2").getString(),
					FontHelper.Palette.STANDARD_CREATE.primary(),
					FontHelper.Palette.STANDARD_CREATE.highlight()
			));
			tooltip.addAll(TooltipHelper.cutStringTextComponent(
					CmiLang.translateDirect("tooltip.solar_boiler.condition.3").getString(),
					FontHelper.Palette.STANDARD_CREATE.primary(),
					FontHelper.Palette.STANDARD_CREATE.highlight()
			));

			tooltip.addAll(TooltipHelper.cutStringTextComponent(
					CmiLang.translateDirect("tooltip.solar_boiler.condition.4").getString(),
					FontHelper.Palette.STANDARD_CREATE.primary(),
					FontHelper.Palette.STANDARD_CREATE.highlight()
			));

			context.addEmpty();
		}
	}

	private void addCtrlTooltip(TooltipContext context) {
		List<Component> tooltip = context.getTooltip();

		CmiLang.isCtrlDown(tooltip);
		if (context.isCtrlDown()) {
			context.addEmpty();

			context.add(CmiLang.translateDirect("tooltip.solar_boiler.info")
					.withStyle(ChatFormatting.GOLD));

			tooltip.addAll(TooltipHelper.cutStringTextComponent(
					CmiLang.translateDirect(
							"tooltip.solar_boiler.efficiency",
							efficiency
					).withStyle(ChatFormatting.GRAY).getString(),
					FontHelper.Palette.STANDARD_CREATE.primary(),
					FontHelper.Palette.STANDARD_CREATE.highlight()
			));

			tooltip.addAll(TooltipHelper.cutStringTextComponent(
					CmiLang.translateDirect(
							"tooltip.solar_boiler.capacity",
							capacity
					).withStyle(ChatFormatting.GRAY).getString(),
					FontHelper.Palette.STANDARD_CREATE.primary(),
					FontHelper.Palette.STANDARD_CREATE.highlight()
			));

			tooltip.addAll(TooltipHelper.cutStringTextComponent(
					CmiLang.translateDirect(
							"tooltip.solar_boiler.total_capacity",
							capacity * 2
					).withStyle(ChatFormatting.GRAY).getString(),
					FontHelper.Palette.STANDARD_CREATE.primary(),
					FontHelper.Palette.STANDARD_CREATE.highlight()
			));
		}
	}
}