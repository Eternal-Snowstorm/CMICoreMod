package dev.celestiacraft.cmi.common.item.mechanism;

import dev.celestiacraft.cmi.Cmi;
import dev.celestiacraft.cmi.common.item.MechanismItem;
import dev.celestiacraft.cmi.common.register.CmiMechanism;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Cmi.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class SculkItem extends MechanismItem {
	private static final int SONIC_BOOM_RANGE = 10;
	private static final double SONIC_BOOM_ANGLE = Math.PI / 13;
	private static final int SONIC_BOOM_COOLDOWN = 100;

	public SculkItem(Properties properties) {
		super(properties);
	}

	@SubscribeEvent
	public static void onRightClickEvent(PlayerInteractEvent.RightClickItem event) {
		Level level = event.getLevel();
		Player player = event.getEntity();
		ItemStack stack = event.getItemStack();

		if (level.isClientSide()) {
			return;
		}

		if (stack.is(CmiMechanism.SCULK.get())) {
			fireSonicBoom((ServerLevel) level, (ServerPlayer) player);
		}
	}

	public static void onInteractEntity(PlayerInteractEvent.EntityInteractSpecific event) {
		Level level = event.getLevel();
		Player player = event.getEntity();

		if (level.isClientSide()) {
			return;
		}

		fireSonicBoom((ServerLevel) level, (ServerPlayer) player);
		event.setCanceled(true);
	}

	private static void fireSonicBoom(ServerLevel level, ServerPlayer player) {
		if (player.getCooldowns().isOnCooldown(CmiMechanism.SCULK.get())) {
			return;
		}

		Vec3 sight = player.getViewVector(1.0f).normalize();
		Vec3 startingPosition = player.getEyePosition();

		level.playSound(
				null,
				startingPosition.x(),
				startingPosition.y(),
				startingPosition.z(),
				SoundEvents.WARDEN_SONIC_BOOM,
				SoundSource.HOSTILE,
				3,
				1
		);

		for (int i = 1; i < SONIC_BOOM_RANGE; i++) {
			Vec3 pos = startingPosition.add(sight.scale(i));
			level.sendParticles(
					ParticleTypes.SONIC_BOOM,
					pos.x(),
					pos.y(),
					pos.z(),
					1,
					0,
					0,
					0,
					0
			);
		}

		level.getEntitiesOfClass(
				LivingEntity.class,
				player.getBoundingBox().inflate(SONIC_BOOM_RANGE)
		).forEach((entity) -> {
			Vec3 direction = entity.getEyePosition().subtract(startingPosition).normalize();

			if (Math.acos(direction.dot(sight)) <= SONIC_BOOM_ANGLE && entity.isAlive()) {
				entity.hurt(player.damageSources().sonicBoom(player), 10);
			}
		});

		player.getCooldowns().addCooldown(CmiMechanism.SCULK.get(), SONIC_BOOM_COOLDOWN);
	}
}