package dev.celestiacraft.cmi.common.item.tool.hammer;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

/**
 * JUST TEST
 */
public class HammerItem extends PickaxeItem {
	public HammerItem(Tier material, Properties properties) {
		super(material, 1, -3.5f, properties);
	}

	@Override
	public float getDestroySpeed(@NotNull ItemStack stack, @NotNull BlockState state) {
		return super.getDestroySpeed(stack, state) / 2.8f;
	}

	@Override
	public boolean mineBlock(@NotNull ItemStack stack, @NotNull Level level, @NotNull BlockState state, @NotNull BlockPos pos, @NotNull LivingEntity entity) {
		super.mineBlock(stack, level, state, pos, entity);

		if (!(entity instanceof ServerPlayer player)) {
			return true;
		}
		if (player.isCrouching()) {
			return true;
		}

		if (!state.is(BlockTags.MINEABLE_WITH_PICKAXE)) {
			return true;
		}

		int X = 0, Y = 0, Z = 0;

		Direction direction = player.getDirection();

		if (direction.getAxis() == Direction.Axis.Y) {
			X = 1;
			Z = 1;
		} else if (direction.getAxis() == Direction.Axis.X) {
			Y = 1;
			Z = 1;
		} else {
			X = 1;
			Y = 1;
		}

		int size = 3;
		int min = -(size / 2);
		int max = (size / 2) + 1;

		int durability = stack.getDamageValue();

		for (int m = min; m < max; m++) {
			for (int n = min; n < max; n++) {
				int x = pos.getX() + m * X;
				int y = pos.getY() + n * Y;
				int z = pos.getZ() + ((Y == 0 ? n : m) * Z);

				BlockPos target = new BlockPos(x, y, z);

				if (target.equals(pos)) {
					continue;
				}

				BlockState targetState = level.getBlockState(target);

				if (targetState.is(BlockTags.MINEABLE_WITH_PICKAXE)) {
					player.gameMode.destroyBlock(target);
				}
			}
		}

		stack.setDamageValue(durability);
		return true;
	}
}