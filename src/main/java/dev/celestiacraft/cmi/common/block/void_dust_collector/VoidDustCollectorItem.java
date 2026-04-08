package dev.celestiacraft.cmi.common.block.void_dust_collector;

import dev.celestiacraft.cmi.api.client.CmiLang;
import dev.celestiacraft.cmi.config.CommonConfig;
import net.createmod.catnip.lang.FontHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

public class VoidDustCollectorItem extends BlockItem {
	public VoidDustCollectorItem(Block block, Properties properties) {
		super(block, properties);
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
		CmiLang.isShiftDown(tooltip);

		if (Screen.hasShiftDown()) {
			int energyConsumption = CommonConfig.VOID_DUST_COLLECTOR_ENERGY_CONSUMPTION.get();
			int workTime = CommonConfig.VOID_DUST_COLLECTOR_WORK_TIME.get();

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