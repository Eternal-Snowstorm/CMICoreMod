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

	// 解析一条声明: 字符串或带属性谓词的对象
	public static @NotNull GrillSource fromJson(@NotNull ResourceLocation fileId, @NotNull JsonElement element) {
		if (element.isJsonPrimitive()) {
			return fromString(fileId, element.getAsString());
		}

		if (!element.isJsonObject()) {
			throw new JsonParseException(String.format("%s: 每条声明必须是字符串或 JSON 对象, 实际是 %s", fileId, element));
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
			throw new JsonParseException(String.format("%s: 声明 %s 缺少 block 或 tag 字段", fileId, json));
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

	// 解析 ns:block 与 #ns:tag 两种字符串形态
	private static @NotNull GrillSource fromString(@NotNull ResourceLocation fileId, @NotNull String raw) {
		String text = raw.trim();

		if (text.isEmpty()) {
			throw new JsonParseException(String.format("%s: 出现空的声明", fileId));
		}

		boolean isTag = text.startsWith("#");
		ResourceLocation id = parseId(fileId, isTag ? text.substring(1) : text);

		return new GrillSource(isTag ? null : id, isTag ? id : null, Map.of(), true);
	}

	// 解析资源位置, 非法时抛解析异常
	private static @NotNull ResourceLocation parseId(@NotNull ResourceLocation fileId, @NotNull String raw) {
		ResourceLocation id = ResourceLocation.tryParse(raw.trim());

		if (id == null) {
			throw new JsonParseException(String.format("%s: %s 不是合法的资源位置", fileId, raw));
		}

		return id;
	}

	// 校验方块已注册与属性存在, empty 表示丢弃
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

	// 列出该方块上不存在的属性名
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

	// 判定方块状态是否命中本条声明
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

	// 属性子集匹配, 未写出的属性不参与判定
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

	// 按属性名取值并与字符串忽略大小写比较
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

	// 取对应方块, 标签声明返回 empty
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

	// 序列化为同步用 JSON
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

	// 人类可读标识, 供日志与反注册使用
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
