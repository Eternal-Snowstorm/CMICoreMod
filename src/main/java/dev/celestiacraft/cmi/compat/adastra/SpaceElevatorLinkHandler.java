package dev.celestiacraft.cmi.compat.adastra;

import com.teamresourceful.resourcefullib.common.utils.SaveHandler;
import earth.terrarium.adastra.api.planets.Planet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class SpaceElevatorLinkHandler extends SaveHandler {
	private static final String LINKS_KEY = "Links";
	private static final String STATION_POS_KEY = "StationPos";
	private static final String ORBIT_ANCHOR_KEY = "OrbitAnchor";
	private static final String GROUND_BASE_KEY = "GroundBase";
	private static final String GROUND_ANCHOR_KEY = "GroundAnchor";
	private static final String GROUND_DIMENSION_KEY = "GroundDimension";
	private static final String ELEVATOR_PRESENT_KEY = "ElevatorPresent";

	private final Map<Long, SpaceElevatorLink> links = new HashMap<>();

	@Override
	public void loadData(CompoundTag tag) {
		links.clear();
		ListTag linkTags = tag.getList(LINKS_KEY, Tag.TAG_COMPOUND);
		for (Tag value : linkTags) {
			CompoundTag linkTag = (CompoundTag) value;
			long stationPos = linkTag.getLong(STATION_POS_KEY);
			SpaceElevatorLink link = new SpaceElevatorLink();
			if (linkTag.contains(ORBIT_ANCHOR_KEY, Tag.TAG_COMPOUND)) {
				link.orbitAnchor = NbtUtils.readBlockPos(linkTag.getCompound(ORBIT_ANCHOR_KEY));
			}
			if (linkTag.contains(GROUND_BASE_KEY, Tag.TAG_COMPOUND)) {
				link.groundBase = NbtUtils.readBlockPos(linkTag.getCompound(GROUND_BASE_KEY));
			}
			if (linkTag.contains(GROUND_ANCHOR_KEY, Tag.TAG_COMPOUND)) {
				link.groundAnchor = NbtUtils.readBlockPos(linkTag.getCompound(GROUND_ANCHOR_KEY));
			}
			if (linkTag.contains(GROUND_DIMENSION_KEY, Tag.TAG_STRING)) {
				link.groundDimension = ResourceKey.create(Registries.DIMENSION, ResourceLocation.parse(linkTag.getString(GROUND_DIMENSION_KEY)));
			}
			link.elevatorPresent = linkTag.getBoolean(ELEVATOR_PRESENT_KEY);
			links.put(stationPos, link);
		}
	}

	@Override
	public void saveData(CompoundTag tag) {
		ListTag linkTags = new ListTag();
		for (Map.Entry<Long, SpaceElevatorLink> entry : links.entrySet()) {
			CompoundTag linkTag = new CompoundTag();
			linkTag.putLong(STATION_POS_KEY, entry.getKey());
			SpaceElevatorLink link = entry.getValue();
			if (link.orbitAnchor != null) {
				linkTag.put(ORBIT_ANCHOR_KEY, NbtUtils.writeBlockPos(link.orbitAnchor));
			}
			if (link.groundBase != null) {
				linkTag.put(GROUND_BASE_KEY, NbtUtils.writeBlockPos(link.groundBase));
			}
			if (link.groundAnchor != null) {
				linkTag.put(GROUND_ANCHOR_KEY, NbtUtils.writeBlockPos(link.groundAnchor));
			}
			if (link.groundDimension != null) {
				linkTag.putString(GROUND_DIMENSION_KEY, link.groundDimension.location().toString());
			}
			if (link.elevatorPresent) {
				linkTag.putBoolean(ELEVATOR_PRESENT_KEY, true);
			}
			linkTags.add(linkTag);
		}
		tag.put(LINKS_KEY, linkTags);
	}

	public static SpaceElevatorLinkHandler read(ServerLevel level) {
		return read(level.getDataStorage(), SpaceElevatorLinkHandler::new, "cmi_space_elevator_links");
	}

	public static void setOrbitAnchor(ServerLevel level, ChunkPos stationPos, BlockPos orbitAnchor) {
		read(level).getOrCreate(stationPos).orbitAnchor = orbitAnchor.immutable();
	}

	public static void setGroundBase(ServerLevel level, ChunkPos stationPos, ResourceKey<Level> groundDimension, BlockPos groundBase) {
		SpaceElevatorLink link = read(level).getOrCreate(stationPos);
		link.groundDimension = groundDimension;
		link.groundBase = groundBase.immutable();
	}

	public static void setGroundAnchor(ServerLevel level, ChunkPos stationPos, BlockPos groundAnchor) {
		read(level).getOrCreate(stationPos).groundAnchor = groundAnchor.immutable();
	}

	public static void setElevatorPresent(ServerLevel level, ChunkPos stationPos, boolean present) {
		read(level).getOrCreate(stationPos).elevatorPresent = present;
	}

	public static boolean isElevatorPresent(ServerLevel level, ChunkPos stationPos) {
		SpaceElevatorLink link = read(level).links.get(stationPos.toLong());
		return link != null && link.elevatorPresent;
	}

	public static void markElevatorPresent(ServerLevel level, BlockPos anchorPos, boolean present) {
		if (Planet.EARTH_ORBIT.equals(level.dimension())) {
			LinkTarget link = findByOrbitDockAnchor(level, anchorPos);
			if (link == null) {
				link = findByOrbitAnchor(level, anchorPos);
			}
			if (link != null) {
				setElevatorPresent(level, link.stationPos(), present);
			}
			return;
		}

		ServerLevel orbitLevel = level.getServer().getLevel(Planet.EARTH_ORBIT);
		if (orbitLevel == null) {
			return;
		}

		LinkTarget link = findByGroundAnchor(orbitLevel, level.dimension(), anchorPos);
		if (link != null) {
			setElevatorPresent(orbitLevel, link.stationPos(), present);
		}
	}

	@Nullable
	public static BlockPos getOrbitAnchor(ServerLevel level, ChunkPos stationPos) {
		SpaceElevatorLink link = read(level).links.get(stationPos.toLong());
		return link == null ? null : link.orbitAnchor;
	}

	@Nullable
	public static BlockPos getGroundBase(ServerLevel level, ChunkPos stationPos) {
		SpaceElevatorLink link = read(level).links.get(stationPos.toLong());
		return link == null ? null : link.groundBase;
	}

	@Nullable
	public static ResourceKey<Level> getGroundDimension(ServerLevel level, ChunkPos stationPos) {
		SpaceElevatorLink link = read(level).links.get(stationPos.toLong());
		return link == null ? null : link.groundDimension;
	}

	@Nullable
	public static LinkTarget findByGroundAnchor(ServerLevel level, ResourceKey<Level> groundDimension, BlockPos groundAnchor) {
		for (Map.Entry<Long, SpaceElevatorLink> entry : read(level).links.entrySet()) {
			SpaceElevatorLink link = entry.getValue();
			if (!groundDimension.equals(link.groundDimension) || link.groundAnchor == null || link.orbitAnchor == null) {
				continue;
			}
			if (link.groundAnchor.equals(groundAnchor)) {
				return new LinkTarget(new ChunkPos(entry.getKey()), link.orbitAnchor, link.groundBase, link.groundAnchor, link.groundDimension);
			}
		}
		return null;
	}

	@Nullable
	public static LinkTarget findByOrbitAnchor(ServerLevel level, BlockPos orbitAnchor) {
		for (Map.Entry<Long, SpaceElevatorLink> entry : read(level).links.entrySet()) {
			SpaceElevatorLink link = entry.getValue();
			if (link.orbitAnchor == null || link.groundDimension == null) {
				continue;
			}
			if (link.orbitAnchor.equals(orbitAnchor)) {
				return new LinkTarget(new ChunkPos(entry.getKey()), link.orbitAnchor, link.groundBase, link.groundAnchor, link.groundDimension);
			}
		}
		return null;
	}

	@Nullable
	public static LinkTarget findByOrbitDockAnchor(ServerLevel level, BlockPos orbitDockAnchor) {
		for (Map.Entry<Long, SpaceElevatorLink> entry : read(level).links.entrySet()) {
			SpaceElevatorLink link = entry.getValue();
			if (link.orbitAnchor == null || link.groundDimension == null) {
				continue;
			}
			if (AdAstraSpaceElevatorTravelCompat.toOrbitDockAnchor(link.orbitAnchor).equals(orbitDockAnchor)) {
				return new LinkTarget(new ChunkPos(entry.getKey()), link.orbitAnchor, link.groundBase, link.groundAnchor, link.groundDimension);
			}
		}
		return null;
	}

	@Nullable
	public static LinkTarget findNearestByGroundBase(ServerLevel level, ResourceKey<Level> groundDimension, BlockPos groundPos, double maxDistance) {
		LinkTarget best = null;
		double bestDistance = maxDistance * maxDistance;
		for (Map.Entry<Long, SpaceElevatorLink> entry : read(level).links.entrySet()) {
			SpaceElevatorLink link = entry.getValue();
			if (!groundDimension.equals(link.groundDimension) || link.groundBase == null || link.orbitAnchor == null) {
				continue;
			}
			double distance = link.groundBase.distSqr(groundPos);
			if (distance > bestDistance) {
				continue;
			}
			bestDistance = distance;
			best = new LinkTarget(new ChunkPos(entry.getKey()), link.orbitAnchor, link.groundBase, link.groundAnchor, link.groundDimension);
		}
		return best;
	}

	private SpaceElevatorLink getOrCreate(ChunkPos stationPos) {
		return links.computeIfAbsent(stationPos.toLong(), key -> new SpaceElevatorLink());
	}

	public static java.util.Collection<SpaceElevatorLink> getAllLinks(ServerLevel level) {
		return read(level).links.values();
	}

	public static class SpaceElevatorLink {
		public BlockPos orbitAnchor;
		BlockPos groundBase;
		public BlockPos groundAnchor;
		ResourceKey<Level> groundDimension;
		public boolean elevatorPresent;
	}

	@Override
	public boolean isDirty() {
		return true;
	}

	public record LinkTarget(ChunkPos stationPos, BlockPos orbitAnchor, @Nullable BlockPos groundBase, @Nullable BlockPos groundAnchor, @Nullable ResourceKey<Level> groundDimension) {
	}
}
