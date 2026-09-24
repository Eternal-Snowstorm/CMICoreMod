package dev.celestiacraft.cmi.common.feature.cargogrid;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.celestiacraft.cmi.Cmi;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.loading.FMLPaths;

import javax.annotation.Nullable;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CargoGridRules {
	private static final String FILE_NAME = "nebula/cmi/cargo_grid.json";
	private static final String ROTATED_NBT_KEY = "CmiCargoGridRotated";

	private static volatile List<CargoGridRule> RULES = Collections.emptyList();
	private static volatile CargoGridDimensions FALLBACK = CargoGridDimensions.UNIT;

	public static Path filePath() {
		return FMLPaths.CONFIGDIR.get().resolve(FILE_NAME);
	}

	public static List<CargoGridRule> rules() {
		return RULES;
	}

	public static CargoGridDimensions fallback() {
		return FALLBACK;
	}

	public static boolean isRotated(ItemStack stack) {
		if (stack.isEmpty()) {
			return false;
		}
		CompoundTag tag = stack.getTag();
		return tag != null && tag.getBoolean(ROTATED_NBT_KEY);
	}

	public static void toggleRotated(ItemStack stack) {
		if (stack.isEmpty()) {
			return;
		}
		CompoundTag tag = stack.getOrCreateTag();
		if (tag.getBoolean(ROTATED_NBT_KEY)) {
			tag.remove(ROTATED_NBT_KEY);
			if (tag.isEmpty()) {
				stack.setTag(null);
			}
		} else {
			tag.putBoolean(ROTATED_NBT_KEY, true);
		}
	}

	public static CargoGridDimensions resolve(ItemStack stack) {
		if (stack.isEmpty()) return CargoGridDimensions.UNIT;
		CargoGridDimensions merged = null;
		for (CargoGridRule rule : RULES) {
			if (rule.matches(stack)) {
				merged = merged == null ? rule.dimensions() : merged.mergeMax(rule.dimensions());
			}
		}
		if (merged == null) merged = FALLBACK;
		if (isRotated(stack) && merged.width() != merged.height()) {
			merged = new CargoGridDimensions(merged.height(), merged.width());
		}
		return merged;
	}

	@Nullable
	public static Integer resolveColor(ItemStack stack) {
		if (stack.isEmpty()) {
			return null;
		}
		for (CargoGridRule rule : RULES) {
			if (rule.matches(stack) && rule.backgroundColor() != null) {
				return rule.backgroundColor();
			}
		}
		return null;
	}

	public static synchronized int load() {
		Path path = filePath();
		try {
			if (!Files.exists(path)) {
				writeDefault(path);
				Cmi.LOGGER.info("[CargoGrid] Default config generated at {}", path);
			}
			String raw = Files.readString(path, StandardCharsets.UTF_8);
			JsonElement root = JsonParser.parseString(raw);
			if (!root.isJsonObject()) {
				throw new IllegalStateException("Root element must be a JSON object");
			}
			parse(root.getAsJsonObject());
			Cmi.LOGGER.info("[CargoGrid] Loaded {} rules from {}", RULES.size(), path);
			return RULES.size();
		} catch (Exception ex) {
			Cmi.LOGGER.error("[CargoGrid] Failed to load {} — falling back to 1x1 for all items", path, ex);
			RULES = Collections.emptyList();
			FALLBACK = CargoGridDimensions.UNIT;
			return -1;
		}
	}

	private static void parse(JsonObject root) {
		List<CargoGridRule> parsed = new ArrayList<>();
		if (root.has("default") && root.get("default").isJsonObject()) {
			FALLBACK = readDimensions(root.getAsJsonObject("default"), CargoGridDimensions.UNIT);
		} else {
			FALLBACK = CargoGridDimensions.UNIT;
		}
		if (root.has("rules") && root.get("rules").isJsonArray()) {
			JsonArray arr = root.getAsJsonArray("rules");
			for (int i = 0; i < arr.size(); i++) {
				JsonElement el = arr.get(i);
				if (!el.isJsonObject()) continue;
				CargoGridRule rule = readRule(el.getAsJsonObject(), i);
				if (rule != null) parsed.add(rule);
			}
		}
		RULES = Collections.unmodifiableList(parsed);
	}

	private static CargoGridRule readRule(JsonObject obj, int index) {
		String match = obj.has("match") ? obj.get("match").getAsString() : "";
		String value = obj.has("value") ? obj.get("value").getAsString() : "";
		if (value.isEmpty()) {
			Cmi.LOGGER.warn("[CargoGrid] Rule #{} has empty 'value', skipped", index);
			return null;
		}
		CargoGridDimensions dims = readDimensions(obj, CargoGridDimensions.UNIT);
		Integer color = obj.has("color") ? parseColor(obj.get("color").getAsString(), index) : null;
		return switch (match) {
			case "item", "id" -> new CargoGridRule.ById(value, dims, color);
			case "tag" -> {
				CargoGridRule.ByTag tagRule = CargoGridRule.ByTag.of(value, dims, color);
				if (tagRule == null)
					Cmi.LOGGER.warn("[CargoGrid] Rule #{} has invalid tag '{}', skipped", index, value);
				yield tagRule;
			}
			case "regex" -> {
				CargoGridRule.ByRegex regexRule = CargoGridRule.ByRegex.of(value, dims, color);
				if (regexRule == null)
					Cmi.LOGGER.warn("[CargoGrid] Rule #{} has invalid regex '{}', skipped", index, value);
				yield regexRule;
			}
			default -> {
				Cmi.LOGGER.warn("[CargoGrid] Rule #{} has unknown match type '{}', skipped", index, match);
				yield null;
			}
		};
	}

	@Nullable
	private static Integer parseColor(String raw, int ruleIndex) {
		if (raw == null) return null;
		String s = raw.trim();
		if (s.isEmpty()) return null;
		if (!s.isEmpty() && s.charAt(0) == '#') s = s.substring(1);
		else if (s.startsWith("0x") || s.startsWith("0X")) s = s.substring(2);
		try {
			long parsed = Long.parseLong(s, 16);
			if (s.length() == 6) parsed |= 0xFF000000L;
			return (int) parsed;
		} catch (NumberFormatException ex) {
			Cmi.LOGGER.warn("[CargoGrid] Rule #{} has invalid color '{}', ignored", ruleIndex, raw);
			return null;
		}
	}

	private static CargoGridDimensions readDimensions(JsonObject obj, CargoGridDimensions def) {
		int w = obj.has("width") ? obj.get("width").getAsInt() : def.width();
		int h = obj.has("height") ? obj.get("height").getAsInt() : def.height();
		return new CargoGridDimensions(w, h);
	}

	private static void writeDefault(Path path) throws IOException {
		Files.createDirectories(path.getParent());
		JsonObject root = new JsonObject();

		JsonObject def = new JsonObject();
		def.addProperty("width", 1);
		def.addProperty("height", 1);
		root.add("default", def);

		JsonArray rules = new JsonArray();
		rules.add(sample("item", "minecraft:diamond_sword", 1, 3, null));
		rules.add(sample("tag", "minecraft:swords", 1, 2, null));
		rules.add(sample("regex", "^minecraft:.*_pickaxe$", 1, 2, "#FFAA22"));
		root.add("rules", rules);

		Files.writeString(path, new GsonBuilder().setPrettyPrinting().create().toJson(root), StandardCharsets.UTF_8);
	}

	private static JsonObject sample(String match, String value, int width, int height, @Nullable String color) {
		JsonObject obj = new JsonObject();
		obj.addProperty("match", match);
		obj.addProperty("value", value);
		obj.addProperty("width", width);
		obj.addProperty("height", height);
		if (color != null) obj.addProperty("color", color);
		return obj;
	}
}
