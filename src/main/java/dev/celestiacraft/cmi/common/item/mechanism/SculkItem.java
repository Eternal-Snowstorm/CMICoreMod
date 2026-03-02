package dev.celestiacraft.cmi.common.item.mechanism;

import dev.celestiacraft.cmi.common.item.MechanismItem;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import static java.lang.Math.PI;

public class SculkItem extends MechanismItem {
	public SculkItem(Properties properties) {
		super(properties);
	}

	@SubscribeEvent
	public static void onRightClickEvent(PlayerInteractEvent.RightClickItem event) {
		int sonicBoobRange = 10;
		double sonicBoomAngle = PI / 13;
		Level level = event.getLevel();
		Player player = event.getEntity();
		ItemStack stack = event.getItemStack();
		ServerLevel serverLevel = (ServerLevel) level;

		if (level.isClientSide()) {
			return;
		}

		if (player.getCooldowns().isOnCooldown(stack.getItem())) {
			return;
		}

		Vec3 sight = player.getEyePosition();
		Vec3 startingPos = player.getEyePosition(1);

		serverLevel.playSound(
				null,
				startingPos.x,
				startingPos.y,
				startingPos.z,
				SoundEvents.WARDEN_SONIC_BOOM,
				SoundSource.PLAYERS,
				3.0f,
				1.0f
		);

		for (int i = 1; i <= sonicBoobRange; i++) {
			Vec3 sonicBoomPos = startingPos.add(sight.scale(i));

			serverLevel.sendParticles(
					ParticleTypes.SONIC_BOOM,
					sonicBoomPos.x,
					sonicBoomPos.y,
					sonicBoomPos.z,
					1,
					0.5,
					0.5,
					0.5,
					0.1
			);
		}

		serverLevel.getEntitiesOfClass(Player.class, player.getBoundingBox().inflate(sonicBoobRange)).forEach(entity -> {
			Vec3 direction = entity.getEyePosition().subtract(startingPos).normalize();

			if (Math.acos(direction.dot(sight)) <= sonicBoomAngle && entity.isAlive()) {
				entity.hurt(level.damageSources().sonicBoom(player), 10);
			}
		});
	}
}
