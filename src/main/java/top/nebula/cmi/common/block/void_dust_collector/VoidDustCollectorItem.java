package top.nebula.cmi.common.block.void_dust_collector;

import com.simibubi.create.foundation.item.TooltipHelper;
import com.simibubi.create.foundation.utility.Lang;
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
import top.nebula.cmi.config.CommonConfig;
import top.nebula.cmi.api.CmiLang;

import javax.annotation.Nullable;
import java.util.List;

public class VoidDustCollectorItem extends BlockItem {
	public VoidDustCollectorItem(Block block, Properties properties) {
		super(block, properties);
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
		Lang.translate("tooltip.holdForDescription", Component.literal("Shift").withStyle(Screen.hasShiftDown() ? ChatFormatting.WHITE : ChatFormatting.GRAY))
				.style(ChatFormatting.DARK_GRAY)
				.addTo(tooltip);

		if (Screen.hasShiftDown()) {
			int energyConsumption = CommonConfig.VOID_DUST_COLLECTOR_ENERGY_CONSUMPTION.get();
			int workTime = CommonConfig.VOID_DUST_COLLECTOR_WORK_TIME.get();

			tooltip.add(Component.empty());

			tooltip.addAll(TooltipHelper.cutStringTextComponent(
					CmiLang.translateDirect("tooltip.void_dust_collector.summary").getString(),
					TooltipHelper.Palette.STANDARD_CREATE
			));

			tooltip.add(Component.empty());

			CmiLang.translate("tooltip.void_dust_collector.isWorking")
					.style(ChatFormatting.GRAY)
					.addTo(tooltip);

			tooltip.addAll(TooltipHelper.cutStringTextComponent(
					CmiLang.translateDirect("tooltip.void_dust_collector.workTime", workTime).getString(),
					TooltipHelper.Palette.STANDARD_CREATE.primary(),
					TooltipHelper.Palette.STANDARD_CREATE.highlight(),
					1
			));

			tooltip.addAll(TooltipHelper.cutStringTextComponent(
					CmiLang.translateDirect("tooltip.void_dust_collector.energyConsumption", energyConsumption).getString(),
					TooltipHelper.Palette.STANDARD_CREATE.primary(),
					TooltipHelper.Palette.STANDARD_CREATE.highlight(),
					1
			));
		}
	}
}