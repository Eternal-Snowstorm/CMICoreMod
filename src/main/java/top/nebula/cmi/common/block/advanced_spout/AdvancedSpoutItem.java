package top.nebula.cmi.common.block.advanced_spout;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import top.nebula.cmi.common.block.void_dust_collector.VoidDustCollectorItem;

import javax.annotation.Nullable;
import java.util.List;

public class AdvancedSpoutItem extends VoidDustCollectorItem {
	public AdvancedSpoutItem(Block block, Properties properties) {
		super(block, properties);
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {

	}
}