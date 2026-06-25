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
import net.minecraftforge.common.Tags;

public class GoldItem extends MechanismItem {
	public GoldItem(Properties properties) {
		super(properties);
	}

	@Override
	protected boolean useAfterConsume() {
		return false;
	}

	/**
	 * 右键 {@link Tags.Blocks#STONE} 或 {@link Tags.Blocks#COBBLESTONE} 有 1% 的概率变成金块
	 * <p>
	 * (谁家点石成金)
	 *
	 * @param context
	 * @return
	 */
	@Override
	protected InteractionResult onMechanismUseOn(UseOnContext context) {
		Player player = context.getPlayer();
		InteractionHand hand = context.getHand();
		Level level = context.getLevel();
		BlockPos pos = context.getClickedPos();
		BlockState state = level.getBlockState(pos);

		if (level.isClientSide()) {
			return InteractionResult.PASS;
		}

		if (state.is(Tags.Blocks.STONE) || state.is(Tags.Blocks.COBBLESTONE)) {
			player.swing(hand);
			if (level.random.nextFloat() < 0.01F) {
				level.setBlockAndUpdate(pos, Blocks.GOLD_BLOCK.defaultBlockState());
			}
		}
		return InteractionResult.SUCCESS;
	}
}