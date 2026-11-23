package dev.celestiacraft.cmi.common.block.space_elevator_base_console.particle;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.Level;

public final class AstronauticDestructionParticles {
	public static final int DIRECT_BREAK_COUNT = 20;
	public static final int LINKED_BREAK_COUNT = 4;

	private AstronauticDestructionParticles() {
	}

	// 玩家直接破坏方块时按全量数量生成粒子
	public static void spawnDirect(Level level, BlockPos pos, BlockState state) {
		spawn(level, pos, state, DIRECT_BREAK_COUNT);
	}

	// 联动拆除方块时按少量数量生成粒子
	public static void spawnLinked(Level level, BlockPos pos, BlockState state) {
		spawn(level, pos, state, LINKED_BREAK_COUNT);
	}

	// 以方块中心为原点向附近客户端广播破坏粒子
	private static void spawn(Level level, BlockPos pos, BlockState state, int count) {
		if (level.isClientSide()) {
			return;
		}
		if (!(level instanceof ServerLevel serverLevel)) {
			return;
		}
		serverLevel.sendParticles(
				new BlockParticleOption(ParticleTypes.BLOCK, state),
				pos.getX() + 0.5D,
				pos.getY() + 0.5D,
				pos.getZ() + 0.5D,
				count,
				0.35D,
				0.35D,
				0.35D,
				0.05D
		);
	}
}
