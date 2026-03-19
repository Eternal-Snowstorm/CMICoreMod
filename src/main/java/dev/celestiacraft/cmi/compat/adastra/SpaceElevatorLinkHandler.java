package dev.celestiacraft.cmi.compat.adastra;

import com.teamresourceful.resourcefullib.common.utils.SaveHandler;
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
	private static final String GROUND_DIMENSION_KEY = "GroundDimension";

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
			if (linkTag.contains(GROUND_DIMENSION_KEY, Tag.TAG_STRING)) {
				link.groundDimension = ResourceKey.create(Registries.DIMENSION, ResourceLocation.parse(linkTag.getString(GROUND_DIMENSION_KEY)));
			}
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
			if (link.groundDimension != null) {
				linkTag.putString(GROUND_DIMENSION_KEY, link.groundDimension.location().toString());
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

	private SpaceElevatorLink getOrCreate(ChunkPos stationPos) {
		return links.computeIfAbsent(stationPos.toLong(), key -> new SpaceElevatorLink());
	}

	@Override
	public boolean isDirty() {
		return true;
	}

	private static class SpaceElevatorLink {
		private BlockPos orbitAnchor;
		private BlockPos groundBase;
		private ResourceKey<Level> groundDimension;
	}
}
