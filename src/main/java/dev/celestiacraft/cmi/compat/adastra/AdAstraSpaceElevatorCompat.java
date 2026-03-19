package dev.celestiacraft.cmi.compat.adastra;

import earth.terrarium.adastra.api.planets.Planet;
import earth.terrarium.adastra.common.registry.ModBlocks;
import earth.terrarium.adastra.common.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.TicketType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class AdAstraSpaceElevatorCompat {
	private static final String ROOT_TAG = "CmiAdAstraSpaceElevator";
	private static final String LAST_LAUNCH_TAG = "LastLaunchOrigin";
	private static final int BASE_RADIUS = 4;

	private AdAstraSpaceElevatorCompat() {
	}

	public static void clearLastLaunchOrigin(ServerPlayer player) {
		CompoundTag persistentData = player.getPersistentData();
		if (!persistentData.contains(ROOT_TAG, Tag.TAG_COMPOUND)) {
			return;
		}

		CompoundTag rootTag = persistentData.getCompound(ROOT_TAG);
		rootTag.remove(LAST_LAUNCH_TAG);
		persistentData.put(ROOT_TAG, rootTag);
	}

	public static void recordLaunchOrigin(ServerPlayer player, BlockPos pos, ResourceKey<Level> dimension) {
		if (!Level.OVERWORLD.equals(dimension)) {
			return;
		}

		CompoundTag persistentData = player.getPersistentData();
		CompoundTag rootTag = persistentData.getCompound(ROOT_TAG);
		CompoundTag launchOriginTag = new CompoundTag();

		launchOriginTag.putString("Dimension", dimension.location().toString());
		launchOriginTag.putInt("X", pos.getX());
		launchOriginTag.putInt("Y", pos.getY());
		launchOriginTag.putInt("Z", pos.getZ());

		rootTag.put(LAST_LAUNCH_TAG, launchOriginTag);
		persistentData.put(ROOT_TAG, rootTag);
	}

	public static boolean hasValidEarthLaunchOrigin(ServerPlayer player) {
		LaunchOrigin launchOrigin = getLastLaunchOrigin(player);
		return launchOrigin != null && Level.OVERWORLD.equals(launchOrigin.dimension());
	}

	@Nullable
	public static BlockPos getLastEarthLaunchOrigin(ServerPlayer player) {
		LaunchOrigin launchOrigin = getLastLaunchOrigin(player);
		if (launchOrigin == null || !Level.OVERWORLD.equals(launchOrigin.dimension())) {
			return null;
		}
		return launchOrigin.pos();
	}

	public static boolean buildEarthBaseFromLastLaunch(ServerPlayer player, ServerLevel orbitLevel) {
		if (!Planet.EARTH_ORBIT.equals(orbitLevel.dimension())) {
			return false;
		}

		LaunchOrigin launchOrigin = getLastLaunchOrigin(player);
		if (launchOrigin == null || !Level.OVERWORLD.equals(launchOrigin.dimension())) {
			return false;
		}

		ServerLevel groundLevel = player.server.getLevel(launchOrigin.dimension());
		if (groundLevel == null) {
			return false;
		}

		groundLevel.getChunkSource().addRegionTicket(TicketType.PORTAL, new ChunkPos(launchOrigin.pos()), 1, launchOrigin.pos());
		buildGroundBase(groundLevel, launchOrigin.pos());
		clearLastLaunchOrigin(player);
		return true;
	}

	@Nullable
	private static LaunchOrigin getLastLaunchOrigin(ServerPlayer player) {
		CompoundTag rootTag = player.getPersistentData().getCompound(ROOT_TAG);
		if (!rootTag.contains(LAST_LAUNCH_TAG, Tag.TAG_COMPOUND)) {
			return null;
		}

		CompoundTag launchOriginTag = rootTag.getCompound(LAST_LAUNCH_TAG);
		String dimensionId = launchOriginTag.getString("Dimension");
		if (!Level.OVERWORLD.location().toString().equals(dimensionId)) {
			return null;
		}

		return new LaunchOrigin(
			Level.OVERWORLD,
			new BlockPos(
				launchOriginTag.getInt("X"),
				launchOriginTag.getInt("Y"),
				launchOriginTag.getInt("Z")
			)
		);
	}

	private static void buildGroundBase(ServerLevel level, BlockPos centerPos) {
		BlockState floorState = ModBlocks.STEEL_PLATING.get().defaultBlockState();
		BlockState centerSlabState = ModBlocks.STEEL_PLATING_SLAB.get().defaultBlockState();
		BlockState supportState = ModBlocks.STEEL_BLOCK.get().defaultBlockState();
		BlockState pillarState = ModBlocks.GLOWING_STEEL_PILLAR.get().defaultBlockState()
				.setValue(RotatedPillarBlock.AXIS, Direction.Axis.Y);

		for (int xOffset = -BASE_RADIUS; xOffset <= BASE_RADIUS; xOffset++) {
			for (int zOffset = -BASE_RADIUS; zOffset <= BASE_RADIUS; zOffset++) {
				BlockPos floorPos = centerPos.offset(xOffset, 0, zOffset);
				boolean isCenterPad = Math.abs(xOffset) <= 1 && Math.abs(zOffset) <= 1;

				level.setBlock(floorPos, isCenterPad ? centerSlabState : floorState, 3);

				if (Math.abs(xOffset) == BASE_RADIUS || Math.abs(zOffset) == BASE_RADIUS) {
					fillSupportColumn(level, floorPos.below(), supportState);
				}
			}
		}

		placeStorageChest(level, centerPos.above());
		placePillar(level, centerPos.offset(BASE_RADIUS, 1, BASE_RADIUS), pillarState, 3);
		placePillar(level, centerPos.offset(BASE_RADIUS, 1, -BASE_RADIUS), pillarState, 3);
		placePillar(level, centerPos.offset(-BASE_RADIUS, 1, BASE_RADIUS), pillarState, 3);
		placePillar(level, centerPos.offset(-BASE_RADIUS, 1, -BASE_RADIUS), pillarState, 3);

		placePillar(level, centerPos.offset(0, 1, BASE_RADIUS), pillarState, 2);
		placePillar(level, centerPos.offset(0, 1, -BASE_RADIUS), pillarState, 2);
		placePillar(level, centerPos.offset(BASE_RADIUS, 1, 0), pillarState, 2);
		placePillar(level, centerPos.offset(-BASE_RADIUS, 1, 0), pillarState, 2);
	}

	private static void placeStorageChest(ServerLevel level, BlockPos pos) {
		level.setBlock(pos, Blocks.CHEST.defaultBlockState(), 3);
		if (level.getBlockEntity(pos) instanceof ChestBlockEntity chest) {
			chest.setItem(0, new ItemStack(ModItems.LAUNCH_PAD.get()));
			chest.setChanged();
		}
	}

	private static void fillSupportColumn(ServerLevel level, BlockPos startPos, BlockState supportState) {
		for (int depth = 0; depth < 6; depth++) {
			BlockPos pos = startPos.below(depth);
			if (!level.getBlockState(pos).isAir()) {
				return;
			}
			level.setBlock(pos, supportState, 3);
		}
	}

	private static void placePillar(ServerLevel level, BlockPos startPos, BlockState pillarState, int height) {
		for (int yOffset = 0; yOffset < height; yOffset++) {
			level.setBlock(startPos.above(yOffset), pillarState, 3);
		}
	}

	private record LaunchOrigin(ResourceKey<Level> dimension, BlockPos pos) {
	}
}
