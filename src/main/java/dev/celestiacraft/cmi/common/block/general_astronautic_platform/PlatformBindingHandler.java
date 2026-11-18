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

	// 按记录坐标与目标平台建立双向绑定, 并取消双方原有绑定
	public static boolean bind(@NotNull ServerLevel targetLevel, @NotNull BlockPos targetPos, @NotNull GlobalPos sourcePos) {
		GeneralAstronauticPlatformBlockEntity target = platformAt(targetLevel, targetPos);
		if (target == null) {
			return false;
		}
		GeneralAstronauticPlatformBlockEntity source = resolve(targetLevel, sourcePos);
		if (source == null) {
			return false;
		}
		Level sourceLevel = source.getLevel();
		if (sourceLevel == null) {
			return false;
		}
		release(source);
		release(target);
		source.setBoundPlatform(GlobalPos.of(targetLevel.dimension(), targetPos));
		target.setBoundPlatform(GlobalPos.of(sourceLevel.dimension(), source.getBlockPos()));
		return true;
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

	// 取消平台的绑定, 同时清掉对端记录, 绑定替换与平台破坏共用
	public static void release(@NotNull GeneralAstronauticPlatformBlockEntity platform) {
		GlobalPos peer = platform.getBoundPlatform();
		if (peer == null) {
			return;
		}
		GeneralAstronauticPlatformBlockEntity peerPlatform = resolve(platform.getLevel(), peer);
		if (peerPlatform != null) {
			peerPlatform.clearBoundPlatform();
		}
		platform.clearBoundPlatform();
	}
}
