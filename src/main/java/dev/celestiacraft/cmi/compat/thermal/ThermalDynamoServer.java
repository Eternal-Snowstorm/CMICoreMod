package dev.celestiacraft.cmi.compat.thermal;

import cofh.thermal.lib.common.block.entity.DynamoBlockEntity;
import dev.celestiacraft.cmi.Cmi;
import dev.celestiacraft.cmi.network.CmiNetwork;
import dev.celestiacraft.cmi.network.s2c.SyncDynamoEnergyRatePacket;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Mod.EventBusSubscriber(modid = Cmi.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ThermalDynamoServer {
	private static final int SCAN_INTERVAL = 20;
	private static final int SCAN_RADIUS = 8;

	private static final Map<ResourceKey<Level>, Map<BlockPos, Integer>> RATES = new HashMap<>();

	@SubscribeEvent
	public static void onLevelTick(TickEvent.LevelTickEvent event) {
		if (event.phase != TickEvent.Phase.END || !(event.level instanceof ServerLevel level)) {
			return;
		}

		Map<BlockPos, Integer> rates = RATES.computeIfAbsent(level.dimension(), key -> new HashMap<>());

		if (level.getGameTime() % SCAN_INTERVAL == 0L) {
			scan(level, rates);
		}

		update(level, rates);
	}

	private static void scan(ServerLevel level, Map<BlockPos, Integer> rates) {
		for (ServerPlayer player : level.players()) {
			int centerX = player.chunkPosition().x;
			int centerZ = player.chunkPosition().z;

			for (int dx = -SCAN_RADIUS; dx <= SCAN_RADIUS; dx++) {
				for (int dz = -SCAN_RADIUS; dz <= SCAN_RADIUS; dz++) {
					LevelChunk chunk = level.getChunkSource().getChunkNow(centerX + dx, centerZ + dz);

					if (chunk == null) {
						continue;
					}

					for (BlockEntity blockEntity : chunk.getBlockEntities().values()) {
						if (!(blockEntity instanceof DynamoBlockEntity dynamo)) {
							continue;
						}

						BlockPos pos = dynamo.getBlockPos();

						if (rates.containsKey(pos)) {
							continue;
						}

						int energyPerTick = dynamo.getCurSpeed();
						rates.put(pos, energyPerTick);
						send(chunk, pos, energyPerTick);
					}
				}
			}
		}
	}

	private static void update(ServerLevel level, Map<BlockPos, Integer> rates) {
		Iterator<Map.Entry<BlockPos, Integer>> iterator = rates.entrySet().iterator();

		while (iterator.hasNext()) {
			Map.Entry<BlockPos, Integer> entry = iterator.next();
			BlockPos pos = entry.getKey();
			LevelChunk chunk = level.getChunkSource().getChunkNow(pos.getX() >> 4, pos.getZ() >> 4);
			BlockEntity blockEntity = chunk == null ? null : chunk.getBlockEntity(pos);

			if (!(blockEntity instanceof DynamoBlockEntity dynamo)) {
				iterator.remove();
				continue;
			}

			int energyPerTick = dynamo.getCurSpeed();

			if (entry.getValue() == energyPerTick) {
				continue;
			}

			entry.setValue(energyPerTick);
			send(chunk, pos, energyPerTick);
		}
	}

	private static void send(LevelChunk chunk, BlockPos pos, int energyPerTick) {
		CmiNetwork.CHANNEL.send(
				PacketDistributor.TRACKING_CHUNK.with(() -> chunk),
				new SyncDynamoEnergyRatePacket(pos, energyPerTick)
		);
	}
}