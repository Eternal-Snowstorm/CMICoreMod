package dev.celestiacraft.cmi.common.item.mechanism;

import dev.celestiacraft.cmi.common.item.MechanismItem;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

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
		Player player = context.getPlayer();
		InteractionHand hand = context.getHand();
		BlockPos pos = context.getClickedPos();
		Level level = context.getLevel();
		BlockState state = level.getBlockState(pos);

		if (state.is(Blocks.COBBLESTONE)) {
			player.swing(hand);
			level.setBlockAndUpdate(pos, Blocks.STONE.defaultBlockState());
			return InteractionResult.SUCCESS;
		}
		return InteractionResult.PASS;
	}
}