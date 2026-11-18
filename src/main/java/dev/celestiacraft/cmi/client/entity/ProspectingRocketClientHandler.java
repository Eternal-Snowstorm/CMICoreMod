package dev.celestiacraft.cmi.client.entity;

import dev.celestiacraft.cmi.common.entity.prospecting_rocket.ProspectingRocketEntity;
import dev.celestiacraft.cmi.common.entity.prospecting_rocket.ProspectingRocketFlightSoundInstance;
import dev.celestiacraft.cmi.common.entity.prospecting_rocket.RocketFlightPhase;
import earth.terrarium.adastra.common.registry.ModParticleTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public final class ProspectingRocketClientHandler {
	private static final int FLAME_COUNT = 20;
	private static final int SMOKE_COUNT = 5;
	private static final int COUNTDOWN_SMOKE_COUNT = 6;
	private static final double PARTICLE_SPREAD = 0.25D;
	private static final double PARTICLE_SPEED = 0.05D;
	private static final double FLAME_OFFSET = 0.75D;

	private ProspectingRocketClientHandler() {
	}

	// 倒计时阶段冒烟, 飞行阶段播放循环音与尾焰
	public static void tickRocket(ProspectingRocketEntity rocket) {
		RocketFlightPhase phase = rocket.getFlightPhase();
		if (phase == RocketFlightPhase.COUNTDOWN) {
			rocket.setFlightSoundStarted(false);
			spawnCountdownSmoke(rocket);
			return;
		}
		if (!rocket.isRocketSoundActive()) {
			rocket.setFlightSoundStarted(false);
			return;
		}
		if (!rocket.isFlightSoundStarted()) {
			Minecraft.getInstance().getSoundManager().play(new ProspectingRocketFlightSoundInstance(rocket));
			rocket.setFlightSoundStarted(true);
		}
		spawnParticles(rocket);
	}

	// 照 Ad Astra 点火冒烟: 每 tick 少量营火烟
	private static void spawnCountdownSmoke(ProspectingRocketEntity rocket) {
		Level level = rocket.level();
		RandomSource random = level.getRandom();
		for (int i = 0; i < COUNTDOWN_SMOKE_COUNT; i++) {
			level.addParticle(ParticleTypes.CAMPFIRE_COSY_SMOKE, rocket.getX(), rocket.getY(), rocket.getZ(), speed(random), speed(random), speed(random));
		}
	}

	private static void spawnParticles(ProspectingRocketEntity rocket) {
		Level level = rocket.level();
		RandomSource random = level.getRandom();
		for (int i = 0; i < FLAME_COUNT; i++) {
			level.addParticle(ModParticleTypes.LARGE_FLAME.get(), x(rocket, random), rocket.getY() - FLAME_OFFSET, z(rocket, random), speed(random), -0.5D, speed(random));
		}
		for (int i = 0; i < SMOKE_COUNT; i++) {
			level.addParticle(ModParticleTypes.LARGE_SMOKE.get(), x(rocket, random), rocket.getY() - FLAME_OFFSET, z(rocket, random), speed(random), -0.5D, speed(random));
		}
	}

	private static double x(ProspectingRocketEntity rocket, RandomSource random) {
		return rocket.getX() + (random.nextDouble() - 0.5D) * PARTICLE_SPREAD * 2.0D;
	}

	private static double z(ProspectingRocketEntity rocket, RandomSource random) {
		return rocket.getZ() + (random.nextDouble() - 0.5D) * PARTICLE_SPREAD * 2.0D;
	}

	private static double speed(RandomSource random) {
		return (random.nextDouble() - 0.5D) * PARTICLE_SPEED * 2.0D;
	}
}
