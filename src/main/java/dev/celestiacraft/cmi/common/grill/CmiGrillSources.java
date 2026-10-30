package dev.celestiacraft.cmi.common.grill;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.logging.LogUtils;
import dev.celestiacraft.cmi.Cmi;
import dev.celestiacraft.cmi.network.CmiNetwork;
import dev.celestiacraft.cmi.network.s2c.SyncGrillSourcesPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.OnDatapackSyncEvent;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class CmiGrillSources {
	public static final String DIRECTORY = "cmi/grill_sources";

	private static final Logger LOGGER = LogUtils.getLogger();
	private static final Gson GSON = new GsonBuilder().setLenient().create();

	// 数据包与运行时都为空时的兜底配置
	private static final String BUILTIN_DEFAULT_JSON = """
			{
			  "entries": [
			    "#cmi:grill_sources",
			    {"block": "minecraft:campfire", "properties": {"lit": "true"}},
			    {"block": "minecraft:soul_campfire", "properties": {"lit": "true"}},
			    {"block": "farmersdelight:stove", "properties": {"lit": "true"}},
			    {"block": "mynethersdelight:nether_stove", "properties": {"lit": "true"}},
			    {"block": "kaleidoscope_cookery:stove", "properties": {"lit": "true"}}
			  ]
			}
			""";

	private static final ResourceLocation BUILTIN_ID = Cmi.loadResource("builtin_grill_sources");

	private static volatile List<GrillSource> datapack = List.of();

	private static final List<GrillSource> runtime = new CopyOnWriteArrayList<>();

	private static volatile List<GrillSource> effective = List.of();

	private static final Map<BlockState, Boolean> CACHE = new ConcurrentHashMap<>();

	// 在 mod 事件总线注册数据包加载器
	public static void onAddReloadListeners(AddReloadListenerEvent event) {
		event.addListener(new GrillSourcesReloadListener());
	}

	// 数据包重载后把整张表同步给客户端
	public static void onDatapackSync(OnDatapackSyncEvent event) {
		SyncGrillSourcesPacket packet = new SyncGrillSourcesPacket(serializeForSync());
		ServerPlayer player = event.getPlayer();

		if (player != null) {
			CmiNetwork.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), packet);
		} else {
			CmiNetwork.CHANNEL.send(PacketDistributor.ALL.noArg(), packet);
		}
	}

	// 判定方块状态是否为炙烤热源, 带缓存
	public static boolean isGrillSource(@NotNull BlockState state) {
		if (state.isAir()) {
			return false;
		}

		Boolean cached = CACHE.get(state);

		if (cached != null) {
			return cached;
		}

		boolean result = false;

		for (GrillSource source : effective) {
			if (source.matches(state)) {
				result = true;
				break;
			}
		}

		CACHE.put(state, result);

		return result;
	}

	// 取 JEI 动画用展示方块, 跳过火焰与流体
	public static @NotNull Optional<Block> displayBlock() {
		Block fallback = null;

		for (GrillSource source : effective) {
			Optional<Block> block = source.resolvedBlock();

			if (block.isEmpty()) {
				continue;
			}

			Block candidate = block.get();

			if (fallback == null) {
				fallback = candidate;
			}

			if (candidate != Blocks.FIRE && candidate != Blocks.SOUL_FIRE && candidate != Blocks.LAVA) {
				return Optional.of(candidate);
			}
		}

		return Optional.ofNullable(fallback);
	}

	// 当前生效的声明快照
	public static @NotNull List<GrillSource> all() {
		return effective;
	}

	// 数据包重载: 解析文件并重建注册表
	public static void apply(@NotNull Map<ResourceLocation, JsonElement> objects) {
		List<FileEntry> files = new ArrayList<>();

		for (Map.Entry<ResourceLocation, JsonElement> entry : objects.entrySet()) {
			ResourceLocation id = entry.getKey();

			try {
				files.add(FileEntry.fromJson(id, entry.getValue()));
			} catch (Exception exception) {
				LOGGER.error("解析 grill_sources 文件 {} 失败, 已跳过", id, exception);
			}
		}

		files.sort(Comparator.comparingInt(FileEntry::priority).thenComparing(entry -> entry.id().toString()));

		List<GrillSource> result = new ArrayList<>();

		for (FileEntry file : files) {
			if (file.replace()) {
				result.clear();
			}

			result.addAll(file.entries());
		}

		if (result.isEmpty()) {
			LOGGER.warn("没有加载到任何 grill_sources 配置, 使用内置默认值");
			result.addAll(parseEntries(BUILTIN_ID, GSON.fromJson(BUILTIN_DEFAULT_JSON, JsonElement.class)));
		}

		datapack = List.copyOf(result);
		rebuild();

		Cmi.LOGGER.info("Loaded {} grill source(s) from {} file(s)", result.size(), files.size());
	}

	// 客户端收到同步包后重建注册表
	public static void applyClient(@NotNull String json) {
		try {
			List<GrillSource> parsed = parseEntries(Cmi.loadResource("synced_grill_sources"), GSON.fromJson(json, JsonElement.class));

			datapack = List.copyOf(parsed);
			runtime.clear();
			rebuild();

			LOGGER.info("Received {} grill source(s) from server", parsed.size());
		} catch (Exception exception) {
			LOGGER.error("解析服务端同步过来的 grill_sources 失败, 继续沿用本地配置", exception);
		}
	}

	// 运行时按方块或标签 id 注册
	public static void register(@NotNull String blockOrTag) {
		register(blockOrTag, Map.of());
	}

	// 运行时注册并附加属性谓词
	public static void register(@NotNull String blockOrTag, @NotNull Map<String, String> properties) {
		GrillSource source = buildRuntimeSource(Cmi.loadResource("runtime"), blockOrTag, properties);

		if (source == null) {
			return;
		}

		runtime.add(source);
		rebuild();

		LOGGER.info("Registered grill source at runtime: {}", source.describe());
	}

	// 运行时按方块实例注册
	public static void registerBlock(@NotNull Block block) {
		registerBlock(block, Map.of());
	}

	// 运行时按方块实例注册并附加属性谓词
	public static void registerBlock(@NotNull Block block, @NotNull Map<String, String> properties) {
		ResourceLocation id = ForgeRegistries.BLOCKS.getKey(block);

		if (id == null) {
			LOGGER.warn("无法为未注册的方块 {} 注册 grill source", block);
			return;
		}

		register(id.toString(), properties);
	}

	// 运行时反向移除已注册条目
	public static boolean unregister(@NotNull String blockOrTag) {
		String normalized = blockOrTag.trim();
		boolean removed = runtime.removeIf(source -> source.describe().startsWith(normalized));

		if (removed) {
			rebuild();
			LOGGER.info("Unregistered grill source at runtime: {}", normalized);
		}

		return removed;
	}

	// 清空运行时注册, 不动数据包声明
	public static void clearRuntime() {
		runtime.clear();
		rebuild();
	}

	// 序列化整张表供同步
	public static @NotNull String serializeForSync() {
		JsonObject root = new JsonObject();
		JsonArray entries = new JsonArray();

		for (GrillSource source : effective) {
			entries.add(source.serialize());
		}

		root.add("entries", entries);

		return GSON.toJson(root);
	}

	// 由运行时入参构造一条声明
	private static GrillSource buildRuntimeSource(ResourceLocation fileId, String blockOrTag, Map<String, String> properties) {
		String text = blockOrTag.trim();

		if (text.isEmpty()) {
			LOGGER.warn("grill source 注册失败: 方块/标签 id 为空");
			return null;
		}

		JsonObject json = new JsonObject();

		if (!text.isEmpty() && text.charAt(0) == '#') {
			json.addProperty("tag", text.substring(1));
		} else {
			json.addProperty("block", text);
		}

		if (!properties.isEmpty()) {
			JsonObject jsonProperties = new JsonObject();
			properties.forEach(jsonProperties::addProperty);
			json.add("properties", jsonProperties);
		}

		try {
			return GrillSource.fromJson(fileId, json).validate(fileId).orElse(null);
		} catch (Exception exception) {
			LOGGER.error("注册 grill source {} 失败", blockOrTag, exception);
			return null;
		}
	}

	// 解析条目数组, 单条失败即跳过
	private static List<GrillSource> parseEntries(ResourceLocation fileId, JsonElement element) {
		if (element == null) {
			return List.of();
		}

		JsonArray array;

		if (element.isJsonArray()) {
			array = element.getAsJsonArray();
		} else if (element.isJsonObject()) {
			JsonObject json = element.getAsJsonObject();
			array = json.has("entries") ? json.getAsJsonArray("entries") : new JsonArray();
		} else {
			throw new JsonParseException(String.format("%s: 顶层必须是 JSON 对象或数组", fileId));
		}

		List<GrillSource> list = new ArrayList<>();
		int index = 0;

		for (JsonElement entry : array) {
			index++;

			try {
				GrillSource source = GrillSource.fromJson(fileId, entry);
				source.validate(fileId).ifPresent(list::add);
			} catch (Exception exception) {
				LOGGER.error("解析 grill_sources {} 第 {} 条声明失败, 已跳过", fileId, index, exception);
			}
		}

		return list;
	}

	// 合并数据包与运行时声明并清空缓存
	private static void rebuild() {
		List<GrillSource> list = new ArrayList<>(datapack.size() + runtime.size());
		list.addAll(datapack);
		list.addAll(runtime);
		effective = List.copyOf(list);
		CACHE.clear();
	}

	private record FileEntry(ResourceLocation id, int priority, boolean replace, List<GrillSource> entries) {
		// 解析单个数据包文件
		static FileEntry fromJson(ResourceLocation id, JsonElement element) {
			int priority = 0;
			boolean replace = false;
			List<GrillSource> entries;

			if (element != null && element.isJsonObject()) {
				JsonObject json = element.getAsJsonObject();
				priority = GsonHelper.getAsInt(json, "priority", 0);
				replace = GsonHelper.getAsBoolean(json, "replace", false);
				entries = parseEntries(id, json);
			} else {
				entries = parseEntries(id, element);
			}

			return new FileEntry(id, priority, replace, entries);
		}
	}
}