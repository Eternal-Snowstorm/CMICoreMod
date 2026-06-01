package dev.celestiacraft.cmi.common.block.wind_vane;

import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class WindVaneManager {
	private static final Map<ResourceKey<Level>, Long2IntOpenHashMap> CHUNK_COUNTS = new ConcurrentHashMap<>();

	public static void add(Level level, BlockPos pos) {
		if (level == null) {
			return;
		}
		Long2IntOpenHashMap map = CHUNK_COUNTS.computeIfAbsent(level.dimension(), (key) -> {
			return new Long2IntOpenHashMap();
		});
		long key = new ChunkPos(pos).toLong();
		synchronized (map) {
			map.addTo(key, 1);
		}
	}

	public static void remove(Level level, BlockPos pos) {
		if (level == null) {
			return;
		}
		Long2IntOpenHashMap map = CHUNK_COUNTS.get(level.dimension());
		if (map == null) {
			return;
		}
		long key = new ChunkPos(pos).toLong();
		synchronized (map) {
			int next = map.addTo(key, -1) - 1;
			if (next <= 0) {
				map.remove(key);
			}
		}
	}

	public static boolean isSealed(Level level, BlockPos pos) {
		if (level == null) {
			return false;
		}
		Long2IntOpenHashMap map = CHUNK_COUNTS.get(level.dimension());
		if (map == null || map.isEmpty()) {
			return false;
		}
		int cx = pos.getX() >> 4;
		int cz = pos.getZ() >> 4;
		synchronized (map) {
			for (int dx = -1; dx <= 1; dx++) {
				for (int dz = -1; dz <= 1; dz++) {
					if (map.containsKey(ChunkPos.asLong(cx + dx, cz + dz))) {
						return true;
					}
				}
			}
		}
		return false;
	}
}