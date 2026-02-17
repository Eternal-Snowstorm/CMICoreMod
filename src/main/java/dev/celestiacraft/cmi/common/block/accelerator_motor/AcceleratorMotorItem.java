package dev.celestiacraft.cmi.common.block.accelerator_motor;

import com.simibubi.create.content.processing.AssemblyOperatorBlockItem;
import com.simibubi.create.foundation.item.TooltipHelper;
import com.simibubi.create.foundation.utility.Lang;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import dev.celestiacraft.cmi.config.CommonConfig;
import dev.celestiacraft.cmi.api.CmiLang;

import javax.annotation.Nullable;
import java.util.List;

public class AcceleratorMotorItem extends AssemblyOperatorBlockItem {
	public AcceleratorMotorItem(Block block, Properties properties) {
		super(block, properties);
	}

	/**
	 * 渲染物品的 Tooltip。
	 * <p>
	 * Create Tooltip 实现：
	 * <ol>
	 *   <li>始终显示 "按住 Shift 查看详情" 提示</li>
	 *   <li>按住 Shift 时显示详细信息，包括摘要、条件和行为描述</li>
	 *   <li>使用 {@code _下划线_} 语法高亮关键信息</li>
	 *   <li>支持动态参数（如蒸汽消耗量）</li>
	 * </ol>
	 *
	 * @param stack   物品堆
	 * @param level   当前世界（可能为 null）
	 * @param tooltip Tooltip 行列表，向其中添加内容
	 * @param flag    Tooltip 标志（普通/高级）
	 */
	@OnlyIn(Dist.CLIENT)
	@Override
	public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
		int maxSpeedValue = CommonConfig.ACCELERATOR_MOTOR_MAX_SPEED.get();
		/*
		 * "按住 [Shift] 查看详情" 提示 - 始终显示
		 * Shift 按下时文字变白，否则为灰色
		 */
		Lang.translate("tooltip.holdForDescription", Component.literal("Shift").withStyle(Screen.hasShiftDown() ? ChatFormatting.WHITE : ChatFormatting.GRAY))
				.style(ChatFormatting.DARK_GRAY)
				.addTo(tooltip);

		if (Screen.hasShiftDown()) {
			/*
			 * 行为行 - 使用 Palette 着色，带缩进（indent = 1）
			 * 支持动态参数 %s，会被 steamCost 替换
			 */
			tooltip.addAll(TooltipHelper.cutStringTextComponent(
					CmiLang.translateDirect("tooltip.accelerator_motor.behaviour1").getString(),
					TooltipHelper.Palette.STANDARD_CREATE.primary(),
					TooltipHelper.Palette.STANDARD_CREATE.highlight(),
					0
			));

			tooltip.addAll(TooltipHelper.cutStringTextComponent(
					CmiLang.translateDirect("tooltip.accelerator_motor.behaviour2", maxSpeedValue).getString(),
					TooltipHelper.Palette.STANDARD_CREATE.primary(),
					TooltipHelper.Palette.STANDARD_CREATE.highlight(),
					0
			));
		}
	}
}