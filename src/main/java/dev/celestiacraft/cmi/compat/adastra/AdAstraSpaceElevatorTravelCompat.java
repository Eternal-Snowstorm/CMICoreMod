package dev.celestiacraft.cmi.compat.adastra;

import earth.terrarium.adastra.api.planets.Planet;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public final class AdAstraSpaceElevatorTravelCompat {
	private static final int GROUND_BASE_LINK_RADIUS = 16;
	private static final int ORBIT_DOCK_DROP = 5;

	private AdAstraSpaceElevatorTravelCompat() {
	}

	public static void bindConstructedGroundAnchor(ServerLevel groundLevel, BlockPos groundAnchor) {
		if (!Level.OVERWORLD.equals(groundLevel.dimension())) {
			return;
		}

		ServerLevel orbitLevel = groundLevel.getServer().getLevel(Planet.EARTH_ORBIT);
		if (orbitLevel == null) {
			return;
		}

		SpaceElevatorLinkHandler.LinkTarget exact = SpaceElevatorLinkHandler.findByGroundAnchor(orbitLevel, groundLevel.dimension(), groundAnchor);
		if (exact != null) {
			return;
		}

		SpaceElevatorLinkHandler.LinkTarget nearest = SpaceElevatorLinkHandler.findNearestByGroundBase(orbitLevel, groundLevel.dimension(), groundAnchor, GROUND_BASE_LINK_RADIUS);
		if (nearest != null) {
			SpaceElevatorLinkHandler.setGroundAnchor(orbitLevel, nearest.stationPos(), groundAnchor);
		}
	}

	@Nullable
	public static TravelTarget resolveEarthOrbitTarget(ServerLevel groundLevel, BlockPos groundAnchor) {
		if (!Level.OVERWORLD.equals(groundLevel.dimension())) {
			return null;
		}

		ServerLevel orbitLevel = groundLevel.getServer().getLevel(Planet.EARTH_ORBIT);
		if (orbitLevel == null) {
			return null;
		}

		SpaceElevatorLinkHandler.LinkTarget exact = SpaceElevatorLinkHandler.findByGroundAnchor(orbitLevel, groundLevel.dimension(), groundAnchor);
		if (exact != null) {
			return new TravelTarget(orbitLevel, exact.stationPos(), toOrbitDockAnchor(exact.orbitAnchor()));
		}

		bindConstructedGroundAnchor(groundLevel, groundAnchor);
		SpaceElevatorLinkHandler.LinkTarget rebound = SpaceElevatorLinkHandler.findByGroundAnchor(orbitLevel, groundLevel.dimension(), groundAnchor);
		if (rebound == null) {
			return null;
		}

		return new TravelTarget(orbitLevel, rebound.stationPos(), toOrbitDockAnchor(rebound.orbitAnchor()));
	}

	@Nullable
	public static TravelTarget resolveGroundTarget(ServerLevel orbitLevel, BlockPos orbitDockAnchor) {
		if (!Planet.EARTH_ORBIT.equals(orbitLevel.dimension())) {
			return null;
		}

		SpaceElevatorLinkHandler.LinkTarget link = SpaceElevatorLinkHandler.findByOrbitDockAnchor(orbitLevel, orbitDockAnchor);
		if (link == null || link.groundDimension() == null || link.groundAnchor() == null) {
			return null;
		}

		ServerLevel groundLevel = orbitLevel.getServer().getLevel(link.groundDimension());
		if (groundLevel == null) {
			return null;
		}

		return new TravelTarget(groundLevel, link.stationPos(), link.groundAnchor());
	}

	public static BlockPos toOrbitDockAnchor(BlockPos orbitAnchor) {
		return orbitAnchor.below(ORBIT_DOCK_DROP);
	}

	public record TravelTarget(ServerLevel level, ChunkPos stationPos, BlockPos targetAnchor) {
	}
}
