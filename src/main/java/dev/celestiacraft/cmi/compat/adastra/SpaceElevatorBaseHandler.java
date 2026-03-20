package dev.celestiacraft.cmi.compat.adastra;

import com.teamresourceful.resourcefullib.common.utils.SaveHandler;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;

import java.util.HashSet;
import java.util.Set;

public class SpaceElevatorBaseHandler extends SaveHandler {
	private static final String BUILT_STATIONS_KEY = "BuiltStations";
	private final Set<Long> builtStations = new HashSet<>();

	@Override
	public void loadData(CompoundTag tag) {
		for (long stationPos : tag.getLongArray(BUILT_STATIONS_KEY)) {
			builtStations.add(stationPos);
		}
	}

	@Override
	public void saveData(CompoundTag tag) {
		long[] values = new long[builtStations.size()];
		int index = 0;
		for (long value : builtStations) {
			values[index++] = value;
		}
		tag.putLongArray(BUILT_STATIONS_KEY, values);
	}

	public static SpaceElevatorBaseHandler read(ServerLevel level) {
		return read(level.getDataStorage(), SpaceElevatorBaseHandler::new, "cmi_space_elevator_bases");
	}

	public static boolean hasBuiltBase(ServerLevel level, ChunkPos stationPos) {
		return read(level).builtStations.contains(stationPos.toLong());
	}

	public static boolean hasBuiltStructure(ServerLevel level, ChunkPos stationPos) {
		return hasBuiltBase(level, stationPos) || SpaceElevatorLinkHandler.getGroundBase(level, stationPos) != null || SpaceElevatorLinkHandler.getOrbitAnchor(level, stationPos) != null;
	}

	public static void markBaseBuilt(ServerLevel level, ChunkPos stationPos) {
		read(level).builtStations.add(stationPos.toLong());
	}

	@Override
	public boolean isDirty() {
		return true;
	}
}
