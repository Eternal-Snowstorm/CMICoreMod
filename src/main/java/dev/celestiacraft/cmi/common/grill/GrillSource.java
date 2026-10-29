package dev.celestiacraft.cmi.common.grill;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.logging.LogUtils;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 一条 "GRILLED(炙烤) 热源" 声明.
 *
 * <p>
 * 一条声明只干一件事: 判定 "某个方块状态是否算炙烤热源". 支持三种形态
 * </p>
 *
 * <pre>
 * 1. 字符串 {@code "ns:block"}                     —— 该方块的任意状态都算
 * 2. 字符串 {@code "#ns:tag"}                       —— 该方块标签里的任意方块、任意状态都算
 * 3. 对象 {@code {"block": "ns:block", "properties": {"lit": "true"}}}
 *                                                     —— 方块 + 状态属性子集匹配
 *                                                     (tag 同样可以配 properties)
 * </pre>
 *
 * <p>
 * 属性是 "子集" 语义: 只校验写出来的属性, 没写的一律不管. 值按字符串比较(不区分大小写),
 * 所以 {@code true} 与 {@code "true"} 等价.
 * </p>
 *
 * <p>
 * {@code lenient}(默认 true)决定 "方块没有该属性时怎么办": true 表示忽略这条属性限制并记一条
 * warn, false 表示整条声明作废. 这条规则是为了让同一份配置能跨 mod 版本使用 —— 某些灶台有
 * {@code lit}, 某些没有.
 * </p>
 */
public final class GrillSource {
	private static final Logger LOGGER = LogUtils.getLogger();

	private final @Nullable ResourceLocation blockId;
	private final @Nullable ResourceLocation tagId;
	private final Map<String, String> properties;
	private final boolean lenient;

	private GrillSource(@Nullable ResourceLocation blockId, @Nullable ResourceLocation tagId, @NotNull Map<String, String> properties, boolean lenient) {
		this.blockId = blockId;
		this.tagId = tagId;
		this.properties = properties;
		this.lenient = lenient;
	}

	/**
	 * 解析一条声明
	 */
	public static @NotNull GrillSource fromJson(@NotNull ResourceLocation fileId, @NotNull JsonElement element) {
		if (element.isJsonPrimitive()) {
			return fromString(fileId, element.getAsString());
		}

		if (!element.isJsonObject()) {
			throw new JsonParseException(String.format("%s: 每条 grill_sources 声明必须是字符串或 JSON 对象, 实际是 %s", fileId, element));
		}

		JsonObject json = element.getAsJsonObject();
		boolean isTag;
		String target;

		if (json.has("block")) {
			isTag = false;
			target = GsonHelper.getAsString(json, "block");
		} else if (json.has("tag")) {
			isTag = true;
			target = GsonHelper.getAsString(json, "tag");
		} else {
			throw new JsonParseException(String.format("%s: 声明 %s 缺少 \"block\" 或 \"tag\" 字段", fileId, json));
		}

		ResourceLocation id = parseId(fileId, target);

		Map<String, String> properties = new LinkedHashMap<>();

		if (json.has("properties")) {
			JsonObject jsonProperties = GsonHelper.getAsJsonObject(json, "properties");

			for (Map.Entry<String, JsonElement> entry : jsonProperties.entrySet()) {
				properties.put(entry.getKey(), GsonHelper.convertToString(entry.getValue(), entry.getKey()));
			}
		}

		boolean lenient = GsonHelper.getAsBoolean(json, "lenient", true);

		return new GrillSource(isTag ? null : id, isTag ? id : null, Map.copyOf(properties), lenient);
	}

	private static @NotNull GrillSource fromString(@NotNull ResourceLocation fileId, @NotNull String raw) {
		String text = raw.trim();

		if (text.isEmpty()) {
			throw new JsonParseException(String.format("%s: 出现空的 grill_sources 声明", fileId));
		}

		boolean isTag = text.startsWith("#");
		ResourceLocation id = parseId(fileId, isTag ? text.substring(1) : text);

		return new GrillSource(isTag ? null : id, isTag ? id : null, Map.of(), true);
	}

	private static @NotNull ResourceLocation parseId(@NotNull ResourceLocation fileId, @NotNull String raw) {
		ResourceLocation id = ResourceLocation.tryParse(raw.trim());

		if (id == null) {
			throw new JsonParseException(String.format("%s: \"%s\" 不是合法的资源位置", fileId, raw));
		}

		return id;
	}

	/**
	 * 加载期校验: 方块是否注册、属性是否真的存在
	 *
	 * <p>
	 * 只对 "具体方块 + 属性谓词" 的声明生效; 标签在加载期无法枚举成员, 直接放行.
	 * 返回 empty 表示这条声明应该被丢弃.
	 * </p>
	 */
	public @NotNull Optional<GrillSource> validate(@NotNull ResourceLocation fileId) {
		if (blockId == null) {
			return Optional.of(this);
		}

		Block block = ForgeRegistries.BLOCKS.getValue(blockId);

		if (block == null || block == Blocks.AIR) {
			LOGGER.warn("grill_sources {}: 方块 {} 未注册, 已跳过该条声明", fileId, blockId);
			return Optional.empty();
		}

		if (properties.isEmpty()) {
			return Optional.of(this);
		}

		List<String> missing = missingProperties(block);

		if (missing.isEmpty()) {
			return Optional.of(this);
		}

		if (!lenient) {
			LOGGER.warn("grill_sources {}: 方块 {} 没有属性 {} 且 lenient=false, 已跳过该条声明", fileId, blockId, missing);
			return Optional.empty();
		}

		LOGGER.warn("grill_sources {}: 方块 {} 没有属性 {}, 已忽略这些属性限制", fileId, blockId, missing);

		Map<String, String> kept = new LinkedHashMap<>(properties);
		missing.forEach(kept::remove);

		return Optional.of(new GrillSource(blockId, tagId, Map.copyOf(kept), lenient));
	}

	private @NotNull List<String> missingProperties(@NotNull Block block) {
		Collection<Property<?>> defined = block.getStateDefinition().getProperties();
		List<String> missing = new ArrayList<>();

		for (String name : properties.keySet()) {
			boolean found = false;

			for (Property<?> property : defined) {
				if (property.getName().equals(name)) {
					found = true;
					break;
				}
			}

			if (!found) {
				missing.add(name);
			}
		}

		return missing;
	}

	/**
	 * 判定一个方块状态是否命中本条声明
	 */
	public boolean matches(@NotNull BlockState state) {
		if (blockId != null) {
			Block block = ForgeRegistries.BLOCKS.getValue(blockId);

			if (block == null || !state.is(block)) {
				return false;
			}
		} else if (tagId != null) {
			if (!state.is(TagKey.create(Registries.BLOCK, tagId))) {
				return false;
			}
		} else {
			return false;
		}

		return matchesProperties(state);
	}

	private boolean matchesProperties(@NotNull BlockState state) {
		if (properties.isEmpty()) {
			return true;
		}

		for (Map.Entry<String, String> entry : properties.entrySet()) {
			if (!matchesProperty(state, entry.getKey(), entry.getValue())) {
				return false;
			}
		}

		return true;
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	private static boolean matchesProperty(@NotNull BlockState state, @NotNull String name, @NotNull String value) {
		for (Property property : state.getProperties()) {
			if (!property.getName().equals(name)) {
				continue;
			}

			Comparable current = state.getValue(property);

			return property.getName(current).equalsIgnoreCase(value);
		}

		return false;
	}

	/**
	 * 本条声明对应的方块(标签声明返回 empty)
	 */
	public @NotNull Optional<Block> resolvedBlock() {
		if (blockId == null) {
			return Optional.empty();
		}

		Block block = ForgeRegistries.BLOCKS.getValue(blockId);

		if (block == null || block == Blocks.AIR) {
			return Optional.empty();
		}

		return Optional.of(block);
	}

	/**
	 * 序列化回 {@link #fromJson} 能接受的形态(供客户端同步使用)
	 */
	public @NotNull JsonObject serialize() {
		JsonObject json = new JsonObject();

		if (blockId != null) {
			json.addProperty("block", blockId.toString());
		} else if (tagId != null) {
			json.addProperty("tag", tagId.toString());
		}

		if (!properties.isEmpty()) {
			JsonObject jsonProperties = new JsonObject();
			properties.forEach(jsonProperties::addProperty);
			json.add("properties", jsonProperties);
		}

		if (!lenient) {
			json.addProperty("lenient", false);
		}

		return json;
	}

	/**
	 * 人类可读的标识, 用于日志与运行时反注册
	 */
	public @NotNull String describe() {
		StringBuilder builder = new StringBuilder();

		if (tagId != null) {
			builder.append('#').append(tagId);
		} else if (blockId != null) {
			builder.append(blockId);
		}

		if (!properties.isEmpty()) {
			builder.append('[');
			boolean first = true;

			for (Map.Entry<String, String> entry : properties.entrySet()) {
				if (!first) {
					builder.append(',');
				}

				builder.append(entry.getKey()).append('=').append(entry.getValue());
				first = false;
			}

			builder.append(']');
		}

		return builder.toString();
	}

	@Override
	public String toString() {
		return "GrillSource{" + describe() + '}';
	}
}
