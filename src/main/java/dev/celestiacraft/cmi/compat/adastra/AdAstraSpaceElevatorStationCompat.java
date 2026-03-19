package dev.celestiacraft.cmi.compat.adastra;

import earth.terrarium.adastra.api.planets.Planet;
import earth.terrarium.adastra.common.recipes.SpaceStationRecipe;
import earth.terrarium.adastra.common.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

public class AdAstraSpaceElevatorStationCompat {
	private static final int SPACE_STATION_Y = 100;
	private static final int BASE_RADIUS = 4;

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
		buildMirroredGroundBase(level, anchorCenter);
		SpaceElevatorLinkHandler.setOrbitAnchor(level, stationChunk, anchorCenter);
	}

	private static void buildMirroredGroundBase(ServerLevel level, BlockPos centerPos) {
		BlockState floorState = ModBlocks.STEEL_PLATING.get().defaultBlockState();
		BlockState centerSlabState = ModBlocks.STEEL_PLATING_SLAB.get().defaultBlockState()
				.setValue(SlabBlock.TYPE, SlabType.TOP);
		BlockState supportState = ModBlocks.STEEL_BLOCK.get().defaultBlockState();
		BlockState pillarState = ModBlocks.GLOWING_STEEL_PILLAR.get().defaultBlockState()
				.setValue(RotatedPillarBlock.AXIS, Direction.Axis.Y);

		for (int xOffset = -BASE_RADIUS; xOffset <= BASE_RADIUS; xOffset++) {
			for (int zOffset = -BASE_RADIUS; zOffset <= BASE_RADIUS; zOffset++) {
				BlockPos floorPos = centerPos.offset(xOffset, 0, zOffset);
				boolean isCenterPad = Math.abs(xOffset) <= 1 && Math.abs(zOffset) <= 1;

				level.setBlock(floorPos, isCenterPad ? centerSlabState : floorState, 3);

				if (Math.abs(xOffset) == BASE_RADIUS || Math.abs(zOffset) == BASE_RADIUS) {
					fillSupportColumnUp(level, floorPos.above(), supportState);
				}
			}
		}

		placePillarDown(level, centerPos.offset(BASE_RADIUS, -1, BASE_RADIUS), pillarState, 3);
		placePillarDown(level, centerPos.offset(BASE_RADIUS, -1, -BASE_RADIUS), pillarState, 3);
		placePillarDown(level, centerPos.offset(-BASE_RADIUS, -1, BASE_RADIUS), pillarState, 3);
		placePillarDown(level, centerPos.offset(-BASE_RADIUS, -1, -BASE_RADIUS), pillarState, 3);

		placePillarDown(level, centerPos.offset(0, -1, BASE_RADIUS), pillarState, 2);
		placePillarDown(level, centerPos.offset(0, -1, -BASE_RADIUS), pillarState, 2);
		placePillarDown(level, centerPos.offset(BASE_RADIUS, -1, 0), pillarState, 2);
		placePillarDown(level, centerPos.offset(-BASE_RADIUS, -1, 0), pillarState, 2);
	}

	private static void fillSupportColumnUp(ServerLevel level, BlockPos startPos, BlockState supportState) {
		for (int depth = 0; depth < 6; depth++) {
			BlockPos pos = startPos.above(depth);
			if (!level.getBlockState(pos).isAir()) {
				return;
			}
			level.setBlock(pos, supportState, 3);
		}
	}

	private static void placePillarDown(ServerLevel level, BlockPos startPos, BlockState pillarState, int height) {
		for (int yOffset = 0; yOffset < height; yOffset++) {
			level.setBlock(startPos.below(yOffset), pillarState, 3);
		}
	}
}
