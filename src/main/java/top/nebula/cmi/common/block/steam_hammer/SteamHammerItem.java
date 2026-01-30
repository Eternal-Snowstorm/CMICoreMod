package top.nebula.cmi.common.block.steam_hammer;

import com.simibubi.create.content.processing.AssemblyOperatorBlockItem;
import com.simibubi.create.foundation.item.TooltipHelper;
import com.simibubi.create.foundation.item.TooltipHelper.Palette;
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
import top.nebula.cmi.config.CommonConfig;
import top.nebula.cmi.utils.CmiLang;

import javax.annotation.Nullable;
import java.util.List;

/**
 *
 * <h2>Tooltip 渲染说明</h2>
 * <p>
 * 本类展示了如何使用 Create 的 Tooltip 系统实现按住 Shift 显示详细信息，
 * 并支持文本高亮着色。
 *
 * <h3>核心组件</h3>
 * <ul>
 *   <li>{@link TooltipHelper#cutStringTextComponent} - 处理文本并支持高亮语法</li>
 *   <li>{@link Palette} - 预设的颜色配置（主色 + 高亮色）</li>
 *   <li>{@link CmiLang} - CMI 的语言工具类，支持动态参数</li>
 * </ul>
 *
 * <h3>高亮语法</h3>
 * <p>
 * 在语言文件中使用下划线 {@code _} 包裹文字来实现高亮：
 * <pre>{@code
 * // 语言文件 (zh_cn.json)
 * "cmi.tooltip.example": "这是_高亮_的文字"
 *
 * // 渲染结果：
 * // "这是" -> 主色 (棕色 0xC9974C)
 * // "高亮" -> 高亮色 (黄色 0xF1DD79)
 * // "的文字" -> 主色
 * }</pre>
 *
 * <h3>可用的 Palette 预设</h3>
 * <ul>
 *   <li>{@code Palette.STANDARD_CREATE} - Create 标准配色（棕色 + 黄色）</li>
 *   <li>{@code Palette.GRAY_AND_WHITE} - 灰色 + 白色</li>
 *   <li>{@code Palette.GRAY_AND_GOLD} - 灰色 + 金色</li>
 *   <li>{@code Palette.BLUE} / {@code GREEN} / {@code RED} / {@code PURPLE} - 各种颜色组合</li>
 * </ul>
 *
 * <h3>使用示例</h3>
 * <pre>{@code
 * // 1. 添加 "按住 Shift 查看详情" 提示（始终显示）
 * Lang.translate("tooltip.holdForDescription",
 *         Component.literal("Shift").withStyle(
 *             Screen.hasShiftDown() ? ChatFormatting.WHITE : ChatFormatting.GRAY))
 *     .style(ChatFormatting.DARK_GRAY)
 *     .addTo(tooltip);
 *
 * // 2. 按住 Shift 时显示详细内容
 * if (Screen.hasShiftDown()) {
 *     // 摘要行 - 使用 Palette 着色
 *     tooltip.addAll(TooltipHelper.cutStringTextComponent(
 *         CmiLang.translateDirect("tooltip.xxx.summary").getString(),
 *         Palette.STANDARD_CREATE));
 *
 *     // 条件行 - 灰色
 *     CmiLang.translate("tooltip.xxx.condition1")
 *         .style(ChatFormatting.GRAY)
 *         .addTo(tooltip);
 *
 *     // 行为行 - 带缩进和动态参数
 *     tooltip.addAll(TooltipHelper.cutStringTextComponent(
 *         CmiLang.translateDirect("tooltip.xxx.behaviour1", dynamicValue).getString(),
 *         Palette.STANDARD_CREATE.primary(),
 *         Palette.STANDARD_CREATE.highlight(),
 *         1));  // indent = 1 表示缩进一格
 * }
 * }</pre>
 *
 * @see TooltipHelper Create 的 Tooltip 工具类
 * @see Palette 颜色配置
 * @see CmiLang CMI 语言工具类
 */
public class SteamHammerItem extends AssemblyOperatorBlockItem {
	public SteamHammerItem(Block block, Properties properties) {
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
		int steamCost = CommonConfig.STEAM_HAMMER_STEAM_CONSUMPTION.get();

		// "按住 [Shift] 查看详情" 提示 - 始终显示
		// Shift 按下时文字变白，否则为灰色
		Lang.translate("tooltip.holdForDescription",
						Component.literal("Shift").withStyle(Screen.hasShiftDown() ? ChatFormatting.WHITE : ChatFormatting.GRAY))
				.style(ChatFormatting.DARK_GRAY)
				.addTo(tooltip);

		if (Screen.hasShiftDown()) {
			tooltip.add(Component.empty());

			// 使用 TooltipHelper.cutStringTextComponent 支持 _高亮_ 语法
			// 语言文件中用 “_” 包裹的文字会显示为高亮色
			tooltip.addAll(TooltipHelper.cutStringTextComponent(
					CmiLang.translateDirect("tooltip.steam_hammer.summary").getString(),
					Palette.STANDARD_CREATE
			));

			tooltip.add(Component.empty());

			// 条件行 - 使用灰色，不需要高亮
			CmiLang.translate("tooltip.steam_hammer.condition1")
					.style(ChatFormatting.GRAY)
					.addTo(tooltip);

			/*
			 * 行为行 - 使用 Palette 着色，带缩进（indent = 1）
			 * 支持动态参数 %s，会被 steamCost 替换
			 */
			tooltip.addAll(TooltipHelper.cutStringTextComponent(
					CmiLang.translateDirect("tooltip.steam_hammer.behaviour1", steamCost).getString(),
					Palette.STANDARD_CREATE.primary(),
					Palette.STANDARD_CREATE.highlight(),
					1
			));

			tooltip.addAll(TooltipHelper.cutStringTextComponent(
					CmiLang.translateDirect("tooltip.steam_hammer.behaviour2", steamCost).getString(),
					Palette.STANDARD_CREATE.primary(),
					Palette.STANDARD_CREATE.highlight(),
					1
			));
		}
	}
}