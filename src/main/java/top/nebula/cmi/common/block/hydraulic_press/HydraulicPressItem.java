package top.nebula.cmi.common.block.hydraulic_press;

import com.simibubi.create.content.processing.AssemblyOperatorBlockItem;
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

import javax.annotation.Nullable;
import java.util.List;

public class HydraulicPressItem extends AssemblyOperatorBlockItem {
	public HydraulicPressItem(Block block, Properties properties) {
		super(block, properties);
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
		int steamCost = CommonConfig.HYDRAULIC_PRESS_STEAM_CONSUMPTION.get();

		if (Screen.hasShiftDown()) {
			tooltip.add(Component.translatable("tooltip.cmi.hydraulic_press.function_1"));
			tooltip.add(Component.translatable("tooltip.cmi.hydraulic_press.function_2", steamCost));
			tooltip.add(Component.translatable("tooltip.cmi.hydraulic_press.function_3", steamCost));
		} else {
			tooltip.add(Component.translatable("tooltip.cmi.shift.check"));
		}
	}
}