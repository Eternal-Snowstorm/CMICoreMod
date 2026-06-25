package dev.celestiacraft.cmi.compat.adastra;

import dev.celestiacraft.cmi.common.block.space_elevator_base_console.SpaceElevatorBaseConsoleBlock;
import dev.celestiacraft.cmi.common.register.block.SpaceElevatorBlocks;
import dev.celestiacraft.cmi.event.PlaceBlockInWorld;
import earth.terrarium.adastra.api.planets.Planet;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.TicketType;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class AdAstraSpaceElevatorCompat {
	private static final String ROOT_TAG = "CmiAdAstraSpaceElevator";
	private static final String LAST_LAUNCH_TAG = "LastLaunchOrigin";
	private static final int BASE_RADIUS = 6;

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
		PlaceBlockInWorld.placeStructure(level, centerPos.getX() - BASE_RADIUS, centerPos.getY(), centerPos.getZ() - BASE_RADIUS, "space_elevator_base");
		BlockPos consolePos = centerPos.offset(GROUND_CONSOLE_OFFSET);
		level.setBlockAndUpdate(consolePos, SpaceElevatorBlocks.SPACE_ELEVATOR_BASE_CONSOLE.getDefaultState());
		SpaceElevatorBaseConsoleBlock.deployStructure(level, consolePos);
	}

	private static final net.minecraft.core.Vec3i GROUND_CONSOLE_OFFSET = new net.minecraft.core.Vec3i(0, 2, 0);

	private record LaunchOrigin(ResourceKey<Level> dimension, BlockPos pos) {
	}
}
