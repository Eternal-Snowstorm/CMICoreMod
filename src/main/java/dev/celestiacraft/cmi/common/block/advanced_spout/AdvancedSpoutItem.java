package dev.celestiacraft.cmi.common.block.advanced_spout;

import com.simibubi.create.content.processing.AssemblyOperatorBlockItem;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

public class AdvancedSpoutItem extends AssemblyOperatorBlockItem {
	public AdvancedSpoutItem(Block block, Properties properties) {
		super(block, properties);
	}

	// TODO 注液器顶部可以防止红石部件, 到时候记得在Tooltip内说明一下
	@OnlyIn(Dist.CLIENT)
	@Override
	public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {

	}
}