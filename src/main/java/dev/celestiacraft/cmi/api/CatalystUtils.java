package dev.celestiacraft.cmi.api;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.SkullBlock;
import net.minecraft.world.level.block.WallSkullBlock;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Optional;

public class CatalystUtils {
	public static Optional<Direction> GetDirection(BlockState state) {
		Optional<Direction> facingFromBlockState = state.getOptionalValue(WallSkullBlock.FACING)
				.filter((facing) -> {
					return facing == Direction.NORTH
							|| facing == Direction.EAST
							|| facing == Direction.SOUTH
							|| facing == Direction.WEST;
				});
		if (facingFromBlockState.isPresent()) {
			return facingFromBlockState;
		} else {
			return state.getOptionalValue(SkullBlock.ROTATION)
					.flatMap((rotation) -> switch (rotation) {
						case 0 -> Optional.of(Direction.NORTH);
						case 4 -> Optional.of(Direction.EAST);
						case 8 -> Optional.of(Direction.SOUTH);
						case 12 -> Optional.of(Direction.WEST);
						default -> Optional.empty();
					});
		}
	}
}