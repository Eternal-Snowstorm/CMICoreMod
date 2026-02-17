package dev.celestiacraft.cmi.common.block.mars_geothermal_vent;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.client.particle.ACParticleRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class MarsGeothermalVentBlockEntity extends BlockEntity {
	private static final double PARTICLE_DIST = 120 * 120;

	private int soundTime = 0;

	public MarsGeothermalVentBlockEntity(BlockEntityType<? extends MarsGeothermalVentBlockEntity> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	public static void particleTick(Level level, BlockPos pos, BlockState state, MarsGeothermalVentBlockEntity entity) {
		Player player = AlexsCaves.PROXY.getClientSidePlayer();
		if (player.distanceToSqr(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) > PARTICLE_DIST || level.random.nextBoolean()) {
			return;
		}
		int smokeType = state.getValue(MarsGeothermalVentBlock.SMOKE_TYPE);
		ParticleOptions particle = ParticleTypes.SMOKE;
		switch (smokeType) {
			case 1:
				particle = level.random.nextInt(3) == 0 ? ParticleTypes.POOF : ACParticleRegistry.WHITE_VENT_SMOKE.get();
				break;
			case 2, 3:
				particle = level.random.nextInt(3) == 0 ? ParticleTypes.SQUID_INK : ACParticleRegistry.BLACK_VENT_SMOKE.get();
				break;
		}
		float x = (level.random.nextFloat() - 0.5F) * 0.25F;
		float z = (level.random.nextFloat() - 0.5F) * 0.25F;
		level.addAlwaysVisibleParticle(
				particle,
				true,
				pos.getX() + 0.5F + x,
				pos.getY() + 1.0F,
				pos.getZ() + 0.5F + z,
				x * 0.15F,
				0.03F + level.random.nextFloat() * 0.2F,
				z * 0.15F
		);
		if (entity.soundTime-- <= 0) {
			entity.soundTime = level.getRandom().nextInt(20) + 30;
			boolean underwater = !state.getFluidState().isEmpty()
					|| !level.getBlockState(pos.above()).getFluidState().isEmpty();
		}
	}
}