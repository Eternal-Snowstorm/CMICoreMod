package dev.celestiacraft.cmi.event.mbd2;

import com.lowdragmc.mbd2.api.recipe.MBDRecipeType;
import com.lowdragmc.mbd2.common.machine.MBDMachine;
import com.lowdragmc.mbd2.common.machine.definition.config.event.MachineOnRecipeWorkingEvent;
import dev.celestiacraft.cmi.Cmi;
import dev.celestiacraft.cmi.config.common.mbd2.ReinforcedCokeOvenConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.security.SecureRandom;

@Mod.EventBusSubscriber(modid = Cmi.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CokeOvenWorking {
	private static final SecureRandom RANDOM = new SecureRandom();

	@SubscribeEvent
	public static void onCokeOvenWorking(MachineOnRecipeWorkingEvent event) {
		MBDMachine machine = event.getMachine();
		Level level = machine.getLevel();
		MBDRecipeType type = machine.getRecipeType();

		if (!(level instanceof ServerLevel serverLevel)) {
			return;
		}

		if (!type.toString().equals("cmi:reinforced_coke_oven")) {
			return;
		}

		if (!ReinforcedCokeOvenConfig.SMOKING_AT_WORKING.get()) {
			return;
		}

		int frequency = ReinforcedCokeOvenConfig.SMOKING_FREQUENCY.get();
		if (level.getGameTime() % frequency != 0) {
			return;
		}

		BlockPos pos = machine.getPos();
		Direction direction = machine.getFrontFacing().orElse(Direction.NORTH);

		for (int i = 0; i < 9; i++) {
			double localX = RANDOM.nextDouble() * 2 - 1;
			double localZ = -1 - RANDOM.nextDouble() * 2;

			double worldX;
			double worldZ;

			switch (direction) {
				case NORTH -> {
					worldX = -localX;
					worldZ = -localZ;
				}
				case EAST -> {
					worldX = localZ;
					worldZ = -localX;
				}
				case WEST -> {
					worldX = -localZ;
					worldZ = localX;
				}
				default -> {
					worldX = localX;
					worldZ = localZ;
				}
			}

			serverLevel.sendParticles(
					ParticleTypes.CAMPFIRE_COSY_SMOKE,
					pos.getX() + worldX,
					pos.getY() + 3 + RANDOM.nextDouble(),
					pos.getZ() + worldZ,
					0,
					0,
					0.07,
					0,
					1
			);
		}
	}
}