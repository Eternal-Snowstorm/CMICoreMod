package dev.celestiacraft.cmi.common.item.mechanism;

import dev.celestiacraft.cmi.api.register.item.MechanismItem;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LightningRodBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

public class CoilItem extends MechanismItem {
	public CoilItem(Properties properties) {
		super(properties);
	}

	@Override
	protected boolean useAfterConsume() {
		return false;
	}

	@Override
	protected InteractionResultHolder<ItemStack> onMechanismUse(Level level, Player player, InteractionHand hand) {
		if (!level.isClientSide()) {
			HitResult hit = player.pick(5.0D, 0.0F, false);

			if (hit.getType().equals(HitResult.Type.BLOCK)) {
				BlockPos pos = ((BlockHitResult) hit).getBlockPos();
				BlockState state = level.getBlockState(pos);

				if (state.getBlock() instanceof LightningRodBlock) {
					LightningBolt lightning = EntityType.LIGHTNING_BOLT.create(level);

					if (lightning != null) {
						lightning.moveTo(
								pos.getX() + 0.5D,
								pos.getY() + 1.0D,
								pos.getZ() + 0.5D
						);
						level.addFreshEntity(lightning);
					}

					return InteractionResultHolder.success(player.getItemInHand(hand));
				}
			}
		}

		return super.onMechanismUse(level, player, hand);
	}
}