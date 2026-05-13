package dev.celestiacraft.cmi.compat.adastra;

import dev.celestiacraft.cmi.common.block.space_elevator_base_console.SpaceElevatorBaseConsoleBlock;
import dev.celestiacraft.cmi.common.register.CmiBlock;
import dev.celestiacraft.cmi.event.PlaceBlockInWorld;
import earth.terrarium.adastra.api.planets.Planet;
import earth.terrarium.adastra.common.recipes.SpaceStationRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

public class AdAstraSpaceElevatorStationCompat {
	private static final int SPACE_STATION_Y = 100;
	private static final int BASE_RADIUS = 3;
	private static final Vec3i ORBIT_CONSOLE_OFFSET = new Vec3i(0, 1, 0);

	private AdAstraSpaceElevatorStationCompat() {
	}

	public static void decorateEarthOrbitStation(ServerLevel level, ChunkPos stationChunk) {
		if (!Planet.EARTH_ORBIT.equals(level.dimension())) {
			return;
		}

		SpaceStationRecipe recipe = SpaceStationRecipe.getSpaceStation(level, level.dimension()).orElse(null);
		if (recipe == null) {
			return;
		}

		StructureTemplate structure = level.getStructureManager().getOrCreate(recipe.structure());
		BlockPos stationOrigin = BlockPos.containing(
				stationChunk.getMiddleBlockX() - (structure.getSize().getX() / 2.0f),
				SPACE_STATION_Y,
				stationChunk.getMiddleBlockZ() - (structure.getSize().getZ() / 2.0f)
		);
		BlockPos anchorCenter = stationOrigin.offset(structure.getSize().getX() / 2, -1, structure.getSize().getZ() / 2);
		buildElevatorTerminal(level, anchorCenter);
		SpaceElevatorLinkHandler.setOrbitAnchor(level, stationChunk, anchorCenter);
	}

	private static void buildElevatorTerminal(ServerLevel level, BlockPos centerPos) {
		PlaceBlockInWorld.placeStructure(level, centerPos.getX() - BASE_RADIUS, centerPos.getY() - 7, centerPos.getZ() - BASE_RADIUS, "space_elevator_terminal");
		BlockPos consolePos = centerPos.offset(ORBIT_CONSOLE_OFFSET);
		level.setBlockAndUpdate(consolePos, CmiBlock.SPACE_ELEVATOR_BASE_CONSOLE.getDefaultState());
		SpaceElevatorBaseConsoleBlock.deployStructure(level, consolePos);
	}
}
