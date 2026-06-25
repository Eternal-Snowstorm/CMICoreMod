package dev.celestiacraft.cmi.common.block.void_dust_collector;

import dev.celestiacraft.cmi.api.client.CmiLang;
import dev.celestiacraft.cmi.config.common.VoidDustCollectorConfig;
import dev.celestiacraft.libs.api.client.context.TooltipContext;
import dev.celestiacraft.libs.api.register.block.BasicBlockItem;
import net.createmod.catnip.lang.FontHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Block;

import java.util.List;

public class VoidDustCollectorItem extends BasicBlockItem {
	public VoidDustCollectorItem(Block block, Properties properties) {
		super(block, properties);
	}

	@Override
	public void addTooltips(TooltipContext context) {
		List<Component> tooltip = context.getTooltip();

		CmiLang.isShiftDown(tooltip);

		if (context.isShiftDown()) {
			int energyConsumption = VoidDustCollectorConfig.ENERGY_CONSUMPTION.get();
			int workTime = VoidDustCollectorConfig.WORK_TIME.get();

			tooltip.add(Component.empty());

			tooltip.addAll(FontHelper.cutStringTextComponent(
					CmiLang.translateDirect("tooltip.void_dust_collector.summary").getString(),
					FontHelper.Palette.STANDARD_CREATE
			));

			tooltip.add(Component.empty());

			CmiLang.translate("tooltip.void_dust_collector.isWorking")
					.style(ChatFormatting.GRAY)
					.addTo(tooltip);

			tooltip.addAll(FontHelper.cutStringTextComponent(
					CmiLang.translateDirect("tooltip.void_dust_collector.workTime", workTime).getString(),
					FontHelper.Palette.STANDARD_CREATE.primary(),
					FontHelper.Palette.STANDARD_CREATE.highlight(),
					1
			));

			tooltip.addAll(FontHelper.cutStringTextComponent(
					CmiLang.translateDirect("tooltip.void_dust_collector.energyConsumption", energyConsumption).getString(),
					FontHelper.Palette.STANDARD_CREATE.primary(),
					FontHelper.Palette.STANDARD_CREATE.highlight(),
					1
			));
		}
	}
}