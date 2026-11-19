package dev.celestiacraft.cmi.common.block.general_astronautic_platform;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.Level;

public class PlatformBindingHandler {
	private static final String TAG_DIMENSION = "Dimension";
	private static final String TAG_X = "X";
	private static final String TAG_Y = "Y";
	private static final String TAG_Z = "Z";

	// 把一个平台坐标写入 NBT
	public static void write(@NotNull CompoundTag tag, @NotNull String key, @NotNull GlobalPos pos) {
		CompoundTag data = new CompoundTag();
		data.putString(TAG_DIMENSION, pos.dimension().location().toString());
		data.putInt(TAG_X, pos.pos().getX());
		data.putInt(TAG_Y, pos.pos().getY());
		data.putInt(TAG_Z, pos.pos().getZ());
		tag.put(key, data);
	}

	// 从 NBT 读出一个平台坐标, 没有数据时返回 null
	@Nullable
	public static GlobalPos read(@NotNull CompoundTag tag, @NotNull String key) {
		if (!tag.contains(key, CompoundTag.TAG_COMPOUND)) {
			return null;
		}
		CompoundTag data = tag.getCompound(key);
		ResourceLocation location = ResourceLocation.tryParse(data.getString(TAG_DIMENSION));
		if (location == null) {
			return null;
		}
		ResourceKey<Level> dimension = ResourceKey.create(Registries.DIMENSION, location);
		BlockPos pos = new BlockPos(data.getInt(TAG_X), data.getInt(TAG_Y), data.getInt(TAG_Z));
		return GlobalPos.of(dimension, pos);
	}

	// 取坐标对应的平台方块实体, 目标区块未加载或方块消失时返回 null
	@Nullable
	public static GeneralAstronauticPlatformBlockEntity resolve(@Nullable Level level, @Nullable GlobalPos pos) {
		if (level == null || pos == null) {
			return null;
		}
		MinecraftServer server = level.getServer();
		if (server == null) {
			return null;
		}
		ServerLevel targetLevel = server.getLevel(pos.dimension());
		if (targetLevel == null) {
			return null;
		}
		if (!targetLevel.isLoaded(pos.pos())) {
			return null;
		}
		if (targetLevel.getBlockEntity(pos.pos()) instanceof GeneralAstronauticPlatformBlockEntity platform) {
			return platform;
		}
		return null;
	}

	// 与目标平台建立双向绑定, 对端未加载时把写入延后到其加载
	public static boolean bind(@NotNull ServerLevel targetLevel, @NotNull BlockPos targetPos, @NotNull GlobalPos sourcePos) {
		GeneralAstronauticPlatformBlockEntity target = platformAt(targetLevel, targetPos);
		if (target == null) {
			return false;
		}
		GlobalPos targetGlobal = GlobalPos.of(targetLevel.dimension(), targetPos);
		GeneralAstronauticPlatformBlockEntity source = resolve(targetLevel, sourcePos);
		release(target);
		if (source != null) {
			release(source);
			source.setBoundPlatform(targetGlobal);
		} else {
			queueBind(targetLevel, sourcePos, targetGlobal);
		}
		target.setBoundPlatform(sourcePos);
		return true;
	}

	// 平台加载时写入缓存的绑定变更, 需在 validate 之前调用
	public static void applyPending(@NotNull GeneralAstronauticPlatformBlockEntity platform) {
		Level level = platform.getLevel();
		PlatformBindingCache cache = cache(level);
		if (level == null || cache == null) {
			return;
		}
		GlobalPos self = GlobalPos.of(level.dimension(), platform.getBlockPos());
		PlatformBindingCache.Entry entry = cache.take(self);
		if (entry == null) {
			return;
		}
		GlobalPos peer = entry.peer();
		if (peer == null) {
			release(platform);
			return;
		}
		if (!peer.equals(platform.getBoundPlatform())) {
			release(platform);
		}
		platform.setBoundPlatform(peer);
	}

	// 区块加载时校验绑定是否成对, 对端已消失或不再指回自己时清除
	public static void validate(@NotNull GeneralAstronauticPlatformBlockEntity platform) {
		GlobalPos peer = platform.getBoundPlatform();
		Level level = platform.getLevel();
		if (peer == null || level == null || level.isClientSide()) {
			return;
		}
		MinecraftServer server = level.getServer();
		if (server == null) {
			return;
		}
		// 对端有待写入的绑定变更时先不判定, 等对端加载后自行写入
		PlatformBindingCache cache = cache(level);
		if (cache != null && cache.hasPending(peer)) {
			return;
		}
		ServerLevel peerLevel = server.getLevel(peer.dimension());
		if (peerLevel == null || !peerLevel.isLoaded(peer.pos())) {
			return;
		}
		if (!(peerLevel.getBlockState(peer.pos()).getBlock() instanceof GeneralAstronauticPlatformBlock)) {
			platform.clearBoundPlatform();
			return;
		}
		GeneralAstronauticPlatformBlockEntity peerPlatform = resolve(level, peer);
		if (peerPlatform == null) {
			return;
		}
		GlobalPos self = GlobalPos.of(level.dimension(), platform.getBlockPos());
		if (!self.equals(peerPlatform.getBoundPlatform())) {
			platform.clearBoundPlatform();
		}
	}

	@Nullable
	private static GeneralAstronauticPlatformBlockEntity platformAt(@NotNull ServerLevel level, @NotNull BlockPos pos) {
		if (level.getBlockEntity(pos) instanceof GeneralAstronauticPlatformBlockEntity platform) {
			return platform;
		}
		return null;
	}

	// 取消平台的绑定, 对端未加载时把清除延后到其加载
	public static void release(@NotNull GeneralAstronauticPlatformBlockEntity platform) {
		GlobalPos peer = platform.getBoundPlatform();
		if (peer == null) {
			return;
		}
		GeneralAstronauticPlatformBlockEntity peerPlatform = resolve(platform.getLevel(), peer);
		if (peerPlatform != null) {
			peerPlatform.clearBoundPlatform();
		} else {
			queueClear(platform.getLevel(), peer);
		}
		platform.clearBoundPlatform();
	}

	// 写入一条待应用的绑定
	private static void queueBind(@Nullable Level level, @NotNull GlobalPos platform, @NotNull GlobalPos peer) {
		PlatformBindingCache cache = cache(level);
		if (cache == null) {
			return;
		}
		cache.queueBind(platform, peer);
	}

	// 写入一条待应用的清除
	private static void queueClear(@Nullable Level level, @NotNull GlobalPos platform) {
		PlatformBindingCache cache = cache(level);
		if (cache == null) {
			return;
		}
		cache.queueClear(platform);
	}

	// 取服务端的绑定缓存, 客户端取不到
	@Nullable
	private static PlatformBindingCache cache(@Nullable Level level) {
		if (level == null || level.isClientSide()) {
			return null;
		}
		MinecraftServer server = level.getServer();
		if (server == null) {
			return null;
		}
		return PlatformBindingCache.get(server);
	}
}
