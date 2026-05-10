package dev.celestiacraft.cmi.common.item.mechanism;

import com.simibubi.create.AllItems;
import dev.celestiacraft.cmi.Cmi;
import dev.celestiacraft.cmi.common.item.MechanismItem;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

public class StoneItem extends MechanismItem {
	public StoneItem(Properties properties) {
		super(properties);
	}

	@Override
	public boolean useAfterConsume() {
		return false;
	}

	@Override
	protected InteractionResult onMechanismUse(UseOnContext context) {
		BlockPos blockPos = context.getClickedPos();
		Level level = context.getLevel();
		BlockState blockState = level.getBlockState(blockPos);
		if (blockState == Blocks.COBBLESTONE.defaultBlockState()) {
			level.setBlockAndUpdate(blockPos, Blocks.STONE.defaultBlockState());
			return InteractionResult.SUCCESS;
		}
		return InteractionResult.PASS;
	}
}