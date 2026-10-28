package dev.celestiacraft.cmi.compat.thermal;

import dev.celestiacraft.cmi.Cmi;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Mod.EventBusSubscriber(modid = Cmi.MODID, value = Dist.CLIENT)
public class ThermalDynamoClient {
	private static final float ENERGY_TO_SPEED = 80.0F;

	private static final Map<BlockPos, Integer> ENERGY_PER_TICK = new HashMap<>();
	private static final Map<BlockPos, Float> STROKE = new HashMap<>();

	private static Level lastLevel;

	@SubscribeEvent
	public static void onClientTick(TickEvent.ClientTickEvent event) {
		if (event.phase != TickEvent.Phase.START) {
			return;
		}

		Minecraft minecraft = Minecraft.getInstance();
		Level level = minecraft.level;

		if (level == null || minecraft.isPaused()) {
			return;
		}

		if (level != lastLevel) {
			lastLevel = level;
			ENERGY_PER_TICK.clear();
			STROKE.clear();
		}

		Iterator<Map.Entry<BlockPos, Integer>> iterator = ENERGY_PER_TICK.entrySet().iterator();

		while (iterator.hasNext()) {
			Map.Entry<BlockPos, Integer> entry = iterator.next();
			BlockPos pos = entry.getKey();

			if (!level.getChunkSource().hasChunk(pos.getX() >> 4, pos.getZ() >> 4)) {
				iterator.remove();
				STROKE.remove(pos);
				continue;
			}

			float speed = entry.getValue() / ENERGY_TO_SPEED;
			STROKE.computeIfAbsent(pos, ThermalDynamoClient::initialStroke);
			STROKE.merge(pos, speed, Float::sum);
		}
	}

	private static float initialStroke(BlockPos pos) {
		return (float) ((((pos.getX() * 89) * pos.getY()) + (pos.getZ() * 67)) % (Math.PI * 20));
	}

	public static void updateEnergyRate(BlockPos pos, int energyPerTick) {
		ENERGY_PER_TICK.put(pos, energyPerTick);
	}

	public static float getSpeed(BlockPos pos) {
		Integer energyPerTick = ENERGY_PER_TICK.get(pos);
		return energyPerTick == null ? 0.0F : energyPerTick / ENERGY_TO_SPEED;
	}

	public static boolean hasStroke(BlockPos pos) {
		return STROKE.containsKey(pos);
	}

	public static float getStroke(BlockPos pos) {
		return STROKE.getOrDefault(pos, 0.0F);
	}
}