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

/**
 * GRILLED(炙烤) 热源注册表.
 *
 * <p>
 * 数据包目录: {@code data/<namespace>/cmi/grill_sources/<name>.json}, 每个文件形如
 * </p>
 *
 * <pre>
 * {
 *   "priority": 0,          // 可选, 数值越小越先应用(默认 0)
 *   "replace": false,       // 可选, true 表示应用本文件前先清空已累积的声明
 *   "entries": [
 *     "minecraft:fire",
 *     "#cmi:grill_sources",
 *     {"block": "minecraft:campfire", "properties": {"lit": "true"}}
 *   ]
 * }
 * </pre>
 *
 * <p>
 * 文件按 (priority, id) 升序应用, 因此高 priority 的外部数据包可以整表覆盖内置默认值
 * (写法: {@code "replace": true}).
 * </p>
 *
 * <p>
 * 除数据包之外还可以走 Java/KubeJS 运行时注册: {@link #register(String)},
 * {@link #register(String, Map)}, {@link #registerBlock(Block)}. 运行时注册不会被数据包重载清掉.
 * </p>
 *
 * <p>
 * 本类同时是服务端与客户端的数据源: 服务端在数据包重载后解析, 再由 {@link #onDatapackSync}
 * 同步给客户端(JEI 动画与客户端侧的 {@code getHeatLevelOf} 需要同一份配置).
 * </p>
 */
public class CmiGrillSources {
	public static final String DIRECTORY = "cmi/grill_sources";

	private static final Logger LOGGER = LogUtils.getLogger();
	private static final Gson GSON = new GsonBuilder().setLenient().create();

	/**
	 * 内置默认值(仅在数据包与运行时注册都为空时兜底)
	 */
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

	/**
	 * 数据包(含 jar 内置 default.json)解析出来的声明
	 */
	private static volatile List<GrillSource> datapack = List.of();

	/**
	 * Java/KubeJS 运行时注册的声明, 不随数据包重载清空
	 */
	private static final List<GrillSource> runtime = new CopyOnWriteArrayList<>();

	/**
	 * 实际生效的声明(datapack + runtime)
	 */
	private static volatile List<GrillSource> effective = List.of();

	/**
	 * 方块状态 -> 是否炙烤热源. 方块状态是单例, 缓存命中率极高
	 */
	private static final Map<BlockState, Boolean> CACHE = new ConcurrentHashMap<>();

	/**
	 * 在 mod 事件总线上注册数据包监听器
	 */
	public static void onAddReloadListeners(AddReloadListenerEvent event) {
		event.addListener(new GrillSourcesReloadListener());
	}

	/**
	 * 数据包重新加载完成(玩家登录 / {@code /reload} 之后), 把配置同步给客户端
	 */
	public static void onDatapackSync(OnDatapackSyncEvent event) {
		SyncGrillSourcesPacket packet = new SyncGrillSourcesPacket(serializeForSync());
		ServerPlayer player = event.getPlayer();

		if (player != null) {
			CmiNetwork.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), packet);
		} else {
			CmiNetwork.CHANNEL.send(PacketDistributor.ALL.noArg(), packet);
		}
	}

	/**
	 * 判定一个方块状态是否算 GRILLED 热源
	 */
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

	/**
	 * 给 JEI 动画用的展示方块: 取第一条能拿到方块实例的声明, 尽量避开火焰/流体这类不适合方块渲染的
	 */
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

	public static @NotNull List<GrillSource> all() {
		return effective;
	}

	/**
	 * 数据包重载时重建注册表
	 */
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

	/**
	 * 客户端收到同步包后重建注册表
	 */
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

	/**
	 * 运行时注册一条声明(供 KubeJS / 其它 mod 调用)
	 *
	 * @param blockOrTag {@code "ns:block"} 或 {@code "#ns:tag"}
	 */
	public static void register(@NotNull String blockOrTag) {
		register(blockOrTag, Map.of());
	}

	public static void register(@NotNull String blockOrTag, @NotNull Map<String, String> properties) {
		GrillSource source = buildRuntimeSource(Cmi.loadResource("runtime"), blockOrTag, properties);

		if (source == null) {
			return;
		}

		runtime.add(source);
		rebuild();

		LOGGER.info("Registered grill source at runtime: {}", source.describe());
	}

	public static void registerBlock(@NotNull Block block) {
		registerBlock(block, Map.of());
	}

	public static void registerBlock(@NotNull Block block, @NotNull Map<String, String> properties) {
		ResourceLocation id = ForgeRegistries.BLOCKS.getKey(block);

		if (id == null) {
			LOGGER.warn("无法为未注册的方块 {} 注册 grill source", block);
			return;
		}

		register(id.toString(), properties);
	}

	/**
	 * 反注册一条运行时声明(只影响 {@link #register} 注册的, 数据包声明不受影响)
	 */
	public static boolean unregister(@NotNull String blockOrTag) {
		String normalized = blockOrTag.trim();
		boolean removed = runtime.removeIf(source -> source.describe().startsWith(normalized));

		if (removed) {
			rebuild();
			LOGGER.info("Unregistered grill source at runtime: {}", normalized);
		}

		return removed;
	}

	public static void clearRuntime() {
		runtime.clear();
		rebuild();
	}

	/**
	 * 序列化成同步用的 JSON
	 */
	public static @NotNull String serializeForSync() {
		JsonObject root = new JsonObject();
		JsonArray entries = new JsonArray();

		for (GrillSource source : effective) {
			entries.add(source.serialize());
		}

		root.add("entries", entries);

		return GSON.toJson(root);
	}

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

	private static void rebuild() {
		List<GrillSource> list = new ArrayList<>(datapack.size() + runtime.size());
		list.addAll(datapack);
		list.addAll(runtime);
		effective = List.copyOf(list);
		CACHE.clear();
	}

	/**
	 * 一个数据包文件
	 */
	private record FileEntry(ResourceLocation id, int priority, boolean replace, List<GrillSource> entries) {
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