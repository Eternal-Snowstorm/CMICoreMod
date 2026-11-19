package dev.celestiacraft.cmi.common.block.general_astronautic_platform;

import java.util.HashMap;
import java.util.Map;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.core.GlobalPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.saveddata.SavedData;

// 未加载平台的绑定变更缓存, 待其加载时写入
public class PlatformBindingCache extends SavedData {
	private static final String DATA_NAME = "cmi_platform_bindings";
	private static final String TAG_ENTRIES = "Entries";
	private static final String TAG_PLATFORM = "Platform";
	private static final String TAG_TARGET = "Target";
	private static final String TAG_CLEAR = "Clear";

	private final Map<GlobalPos, Entry> entries = new HashMap<>();

	public static PlatformBindingCache get(@NotNull MinecraftServer server) {
		return server.overworld()
				.getDataStorage()
				.computeIfAbsent(PlatformBindingCache::load, PlatformBindingCache::new, DATA_NAME);
	}

	// 记录一次待写入的绑定
	public void queueBind(@NotNull GlobalPos platform, @NotNull GlobalPos peer) {
		entries.put(platform, new Entry(peer));
		setDirty();
	}

	// 记录一次待写入的清除
	public void queueClear(@NotNull GlobalPos platform) {
		entries.put(platform, new Entry(null));
		setDirty();
	}

	// 该平台是否有待写入的变更
	public boolean hasPending(@NotNull GlobalPos platform) {
		return entries.containsKey(platform);
	}

	// 取出并移除一条待写入记录
	@Nullable
	public Entry take(@NotNull GlobalPos platform) {
		Entry entry = entries.remove(platform);
		if (entry == null) {
			return null;
		}
		setDirty();
		return entry;
	}

	@Override
	public @NotNull CompoundTag save(@NotNull CompoundTag tag) {
		ListTag list = new ListTag();
		for (Map.Entry<GlobalPos, Entry> entry : entries.entrySet()) {
			CompoundTag data = new CompoundTag();
			PlatformBindingHandler.write(data, TAG_PLATFORM, entry.getKey());
			GlobalPos peer = entry.getValue().peer();
			if (peer != null) {
				PlatformBindingHandler.write(data, TAG_TARGET, peer);
			} else {
				data.putBoolean(TAG_CLEAR, true);
			}
			list.add(data);
		}
		tag.put(TAG_ENTRIES, list);
		return tag;
	}

	public static PlatformBindingCache load(@NotNull CompoundTag tag) {
		PlatformBindingCache cache = new PlatformBindingCache();
		ListTag list = tag.getList(TAG_ENTRIES, Tag.TAG_COMPOUND);
		for (int i = 0; i < list.size(); i++) {
			CompoundTag data = list.getCompound(i);
			GlobalPos platform = PlatformBindingHandler.read(data, TAG_PLATFORM);
			if (platform == null) {
				continue;
			}
			cache.entries.put(platform, new Entry(PlatformBindingHandler.read(data, TAG_TARGET)));
		}
		return cache;
	}

	// 一条待写入记录, peer 为 null 表示清除绑定
	public record Entry(@Nullable GlobalPos peer) {
	}
}
