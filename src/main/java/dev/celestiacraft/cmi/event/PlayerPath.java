package dev.celestiacraft.cmi.event;

import dev.celestiacraft.cmi.Cmi;
import dev.celestiacraft.cmi.tags.CmiBlockTags;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.UUID;

@Mod.EventBusSubscriber(modid = Cmi.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class PlayerPath {
	private static final UUID PATH_SPEED_UUID = UUID.fromString("A6F82C41-7D93-4E15-9B62-3A5C8D1E740F");

	@SubscribeEvent
	public static void onLivingTick(LivingEvent.LivingTickEvent event) {
		LivingEntity entity = event.getEntity();

		if (!(entity instanceof Player)) {
			return;
		}

		Level level = entity.level();

		if (level.isClientSide()) {
			return;
		}

		// 0.5s检测一次
		if (entity.tickCount % 10 != 0) {
			return;
		}

		BlockPos below = entity.blockPosition().below();
		BlockState state = level.getBlockState(below);

		AttributeInstance attribute = entity.getAttribute(Attributes.MOVEMENT_SPEED);

		if (attribute == null) {
			return;
		}

		if (state.is(CmiBlockTags.PLAYER_PATH)) {
			if (attribute.getModifier(PATH_SPEED_UUID) == null) {
				double bonus = 0.5 + entity.getRandom().nextDouble() * 0.25;
				// 调试用的, 别搞😡
				// double bonus = 1;

				AttributeModifier modifier = new AttributeModifier(
						PATH_SPEED_UUID,
						"Path movement speed bonus",
						bonus,
						AttributeModifier.Operation.MULTIPLY_TOTAL
				);

				attribute.addTransientModifier(modifier);
			}
		} else {
			attribute.removeModifier(PATH_SPEED_UUID);
		}
	}
}